package com.example.bookingservice.controller;

import com.example.bookingservice.dto.BookingRequestDto;
import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.entity.Role;
import com.example.bookingservice.entity.User;
import com.example.bookingservice.repository.BookingRepository;
import com.example.bookingservice.service.BookingService;
import com.example.bookingservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    public BookingController(BookingService bookingService, UserService userService, BookingRepository bookingRepository) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestBody BookingRequestDto dto,
            @RequestHeader("Authorization") String token, // Захватываем токен из заголовка
            Authentication auth) {
        Booking booking = bookingService.createBooking(dto, token, auth.getName());
        return ResponseEntity.ok(booking);
    }

    // --- USER: История бронирований ---
    @GetMapping
    public Page<Booking> getMyBookings(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        User user = userService.getUserEntity(auth.getName());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return bookingRepository.findByUserId(user.getId(), pageable);
    }

    // --- USER: Получение конкретного бронирования ---
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable Long id, Authentication auth) {
        Optional<Booking> bookingOpt = bookingService.findById(id);

        if (bookingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = bookingOpt.get();
        User currentUser = userService.getUserEntity(auth.getName());

        if (currentUser.getRole() == Role.ADMIN || booking.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.ok(booking);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // --- USER: Отмена бронирования ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token, // Захватываем токен из заголовка
            Authentication auth) {
        bookingService.cancelBooking(id, token, auth.getName());
        return ResponseEntity.ok().build();
    }
}