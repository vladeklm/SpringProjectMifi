package com.example.bookingservice.controller;

import com.example.bookingservice.entity.User;
import com.example.bookingservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user") // Как в ТЗ
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // POST /user - создать пользователя (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Для простоты пароль передаем в теле как обычное поле
        User created = userService.createUser(user.getUsername(), user.getPassword());
        return ResponseEntity.ok(created);
    }

    // PATCH /user/{id} - обновить пользователя (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {
        User updated = userService.updateUser(id, user.getUsername(), user.getPassword(), user.getRole().name());
        return ResponseEntity.ok(updated);
    }

    // DELETE /user/{id} - удалить пользователя (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}