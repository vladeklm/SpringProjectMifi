package com.example.bookingservice.service;

import com.example.bookingservice.dto.AvailabilityRequest;
import com.example.bookingservice.dto.BookingRequestDto;
import com.example.bookingservice.dto.RoomDto;
import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.entity.BookingStatus;
import com.example.bookingservice.entity.Role;
import com.example.bookingservice.entity.User;
import com.example.bookingservice.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final WebClient.Builder webClientBuilder;
    private final UserService userService;

    public BookingService(BookingRepository bookingRepository, WebClient.Builder webClientBuilder, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.webClientBuilder = webClientBuilder;
        this.userService = userService;
    }

    @Transactional
    public Booking createBooking(BookingRequestDto dto, String username) {
        // ИСПРАВЛЕНИЕ: Получаем реального пользователя и его ID
        User user = userService.getUserEntity(username);

        String requestId = java.util.UUID.randomUUID().toString();

        Long targetRoomId = dto.getRoomId();
        if (dto.isAutoSelect()) {
            targetRoomId = findBestRoom(dto.getStartDate(), dto.getEndDate());
            if (targetRoomId == null) throw new RuntimeException("No rooms available");
        }

        Booking booking = new Booking();
        booking.setUserId(user.getId()); // Используем реальный ID
        booking.setRoomId(targetRoomId);
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setStatus(BookingStatus.PENDING);
        booking.setRequestId(requestId);
        booking = bookingRepository.save(booking);

        boolean confirmed = tryConfirmAvailability(targetRoomId, dto.getStartDate(), dto.getEndDate(), requestId);

        if (confirmed) {
            booking.setStatus(BookingStatus.CONFIRMED);
        } else {
            booking.setStatus(BookingStatus.CANCELLED);
            releaseRoom(targetRoomId, requestId);
        }
        return bookingRepository.save(booking);
    }

    // НОВЫЙ МЕТОД: Получение истории бронирований
    public List<Booking> getMyBookings(String username) {
        User user = userService.getUserEntity(username);
        return bookingRepository.findByUserId(user.getId());
    }

    private Long findBestRoom(java.time.LocalDate start, java.time.LocalDate end) {
        return webClientBuilder.build()
                .get()
                .uri("lb://hotel-service/api/rooms/recommend?start={start}&end={end}", start, end)
                .retrieve()
                .bodyToFlux(RoomDto.class)
                .next()
                .map(RoomDto::getId)
                .block();
    }

    private boolean tryConfirmAvailability(Long roomId, java.time.LocalDate start, java.time.LocalDate end, String requestId) {
        return webClientBuilder.build()
                .post()
                .uri("lb://hotel-service/api/rooms/" + roomId + "/confirm-availability")
                .bodyValue(new AvailabilityRequest(start, end, requestId))
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(5))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500)))
                .block();
    }

    private void releaseRoom(Long roomId, String requestId) {
        webClientBuilder.build()
                .post()
                .uri("lb://hotel-service/api/rooms/" + roomId + "/release")
                .bodyValue(new AvailabilityRequest(null, null, requestId))
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    @Transactional
    public Booking cancelBooking(Long bookingId, String username) {
        // Находим бронирование
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Проверяем права (User может отменить только СВОЕ, Admin - любое)
        User currentUser = userService.getUserEntity(username);
        if (currentUser.getRole() != Role.ADMIN && !booking.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Если уже отменено, ничего не делаем (идемпотентность)
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return booking;
        }

        // Меняем статус
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Компенсация: освобождаем номер в Hotel Service
        releaseRoom(booking.getRoomId(), booking.getRequestId());

        return booking;
    }
}