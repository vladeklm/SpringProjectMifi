package com.example.bookingservice.controller;

import com.example.bookingservice.dto.BookingRequestDto;
import com.example.bookingservice.entity.Booking;
import com.example.bookingservice.entity.Role;
import com.example.bookingservice.entity.User;
import com.example.bookingservice.service.BookingService;
import com.example.bookingservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserService userService; // 1. Внедряем UserService

    // 2. Обновляем конструктор
    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequestDto dto, Authentication auth) {
        Booking booking = bookingService.createBooking(dto, auth.getName());
        return ResponseEntity.ok(booking);
    }

    // --- USER: История бронирований ---
    @GetMapping
    public List<Booking> getMyBookings(Authentication auth) {
        return bookingService.getMyBookings(auth.getName());
    }

    // --- USER: Получение конкретного бронирования ---
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBooking(@PathVariable Long id, Authentication auth) {
        // 3. Реализуем проверку безопасности
        Optional<Booking> bookingOpt = bookingService.findById(id);

        if (bookingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Booking booking = bookingOpt.get();

        // Получаем сущность текущего пользователя из токена
        User currentUser = userService.getUserEntity(auth.getName());

        // ПРОВЕРКА:
        // 1. Если пользователь Админ -> Доступ разрешен.
        // 2. Если ID пользователя в бронировании совпадает с ID текущего пользователя -> Доступ разрешен.
        // Иначе -> 403 Forbidden.
        if (currentUser.getRole() == Role.ADMIN || booking.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.ok(booking);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}