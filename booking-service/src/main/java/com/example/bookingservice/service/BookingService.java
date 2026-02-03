package com.example.bookingservice.service;

import com.example.bookingservice.dto.AvailabilityRequest;
import com.example.bookingservice.dto.BookingRequestDto;
import com.example.bookingservice.dto.RoomDto;
import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.entity.BookingStatus;
import com.example.bookingservice.entity.Role;
import com.example.bookingservice.entity.User;
import com.example.bookingservice.repository.BookingRepository;
import jakarta.servlet.http.HttpServletRequest; // Обратите внимание на jakarta (для Spring Boot 3)
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final RestClient.Builder restClientBuilder;

    // Внедряем текущий запрос
    private final HttpServletRequest request;

    public BookingService(BookingRepository bookingRepository,
                          UserService userService,
                          RestClient.Builder restClientBuilder,
                          HttpServletRequest request) { // Добавляем в конструктор
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.restClientBuilder = restClientBuilder;
        this.request = request;
    }

    @Transactional
    public Booking createBooking(BookingRequestDto dto, String username) {
        // Берем токен прямо из текущего запроса
        String token = request.getHeader("Authorization");

        User user = userService.getUserEntity(username);
        String requestId = java.util.UUID.randomUUID().toString();

        Long targetRoomId = dto.getRoomId();
        if (dto.isAutoSelect()) {
            targetRoomId = findBestRoom(dto.getStartDate(), dto.getEndDate(), token);
            if (targetRoomId == null) throw new RuntimeException("No rooms available");
        }

        Booking booking = new Booking();
        booking.setUserId(user.getId());
        booking.setRoomId(targetRoomId);
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setStatus(BookingStatus.PENDING);
        booking.setRequestId(requestId);
        booking = bookingRepository.save(booking);

        boolean confirmed = tryConfirmAvailability(targetRoomId, dto.getStartDate(), dto.getEndDate(), requestId, token);

        if (confirmed) {
            booking.setStatus(BookingStatus.CONFIRMED);
        } else {
            booking.setStatus(BookingStatus.CANCELLED);
            releaseRoom(targetRoomId, requestId, token);
        }
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(Long bookingId, String username) {
        // Берем токен прямо из текущего запроса
        String token = request.getHeader("Authorization");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        User currentUser = userService.getUserEntity(username);
        if (currentUser.getRole() != Role.ADMIN && !booking.getUserId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return booking;
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Передаем токен при освобождении комнаты
        releaseRoom(booking.getRoomId(), booking.getRequestId(), token);
        return booking;
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    private Long findBestRoom(java.time.LocalDate start, java.time.LocalDate end, String token) {
        return restClientBuilder.build()
                .get()
                .uri("lb://hotel-service/api/rooms/recommend?start={start}&end={end}", start, end)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RoomDto>>() {})
                .stream()
                .findFirst()
                .map(RoomDto::getId)
                .orElse(null);
    }

    private boolean tryConfirmAvailability(Long roomId, java.time.LocalDate start, java.time.LocalDate end, String requestId, String token) {
        try {
            return restClientBuilder.build()
                    .post()
                    .uri("lb://hotel-service/api/rooms/" + roomId + "/confirm-availability")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", token)
                    .body(new AvailabilityRequest(start, end, requestId))
                    .retrieve()
                    .body(Boolean.class);

        } catch (org.springframework.web.client.RestClientException e) {
            System.err.println("Hotel Service call failed: " + e.getMessage());

            if (e.getMessage().contains("Conflict") || e.getMessage().contains("conflict")) {
                return false;
            }

            return false;
        }
    }

    private void releaseRoom(Long roomId, String requestId, String token) {
        try {
            restClientBuilder.build()
                    .post()
                    .uri("lb://hotel-service/api/rooms/" + roomId + "/release")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", token)
                    .body(new AvailabilityRequest(null, null, requestId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            System.err.println("Error during release: " + e.getMessage());
        }
    }
}