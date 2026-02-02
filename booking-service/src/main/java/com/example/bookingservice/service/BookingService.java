package com.example.bookingservice.service;

import com.example.bookingservice.dto.AvailabilityRequest;
import com.example.bookingservice.dto.BookingRequestDto;
import com.example.bookingservice.dto.RoomDto;
import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.entity.BookingStatus;
import com.example.bookingservice.entity.Role;
import com.example.bookingservice.entity.User;
import com.example.bookingservice.repository.BookingRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final RestClient.Builder restClientBuilder; // Меняем на RestClient

    public BookingService(BookingRepository bookingRepository, UserService userService, RestClient.Builder restClientBuilder) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.restClientBuilder = restClientBuilder;
    }

    @Transactional// Стандартная Spring Retry аннотация
    public Booking createBooking(BookingRequestDto dto, String username) {
        User user = userService.getUserEntity(username);
        String requestId = java.util.UUID.randomUUID().toString();

        Long targetRoomId = dto.getRoomId();
        if (dto.isAutoSelect()) {
            targetRoomId = findBestRoom(dto.getStartDate(), dto.getEndDate());
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

        boolean confirmed = tryConfirmAvailability(targetRoomId, dto.getStartDate(), dto.getEndDate(), requestId);

        if (confirmed) {
            booking.setStatus(BookingStatus.CONFIRMED);
        } else {
            booking.setStatus(BookingStatus.CANCELLED);
            releaseRoom(targetRoomId, requestId);
        }
        return bookingRepository.save(booking);
    }

    @Transactional
    public Booking cancelBooking(Long bookingId, String username) {
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
        releaseRoom(booking.getRoomId(), booking.getRequestId());
        return booking;
    }

    private Long findBestRoom(java.time.LocalDate start, java.time.LocalDate end) {
        // RestClient очень простой:
        return restClientBuilder.build()
                .get()
                .uri("lb://hotel-service/api/rooms/recommend?start={start}&end={end}", start, end)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RoomDto>>() {})
                .stream()
                .findFirst()
                .map(RoomDto::getId)
                .orElse(null);
    }

    private boolean tryConfirmAvailability(Long roomId, java.time.LocalDate start, java.time.LocalDate end, String requestId) {
        try {
            return restClientBuilder.build()
                    .post()
                    .uri("lb://hotel-service/api/rooms/" + roomId + "/confirm-availability")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AvailabilityRequest(start, end, requestId))
                    .retrieve()
                    .body(Boolean.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                return false;
            }
            throw new RuntimeException(e);
        }
    }

    private void releaseRoom(Long roomId, String requestId) {
        try {
            restClientBuilder.build()
                    .post()
                    .uri("lb://hotel-service/api/rooms/" + roomId + "/release")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AvailabilityRequest(null, null, requestId))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            System.err.println("Error during release: " + e.getMessage());
        }
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }
}