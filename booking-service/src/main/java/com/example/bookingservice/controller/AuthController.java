package com.example.bookingservice.controller;

import com.example.bookingservice.dto.AuthRequestDto;
import com.example.bookingservice.dto.AuthResponseDto;
import com.example.bookingservice.dto.RegisterRequestDto;
import com.example.bookingservice.entity.User;
import com.example.bookingservice.security.JwtUtil;
import com.example.bookingservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // POST /user/register
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        User user = userService.createUser(request.getUsername(), request.getPassword());
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponseDto(token));
    }

    // POST /user/auth
    @PostMapping("/auth")
    public ResponseEntity<AuthResponseDto> auth(@Valid @RequestBody AuthRequestDto request) {
        // Используем getUserEntity, чтобы получить сущность БД
        User user = userService.getUserEntity(request.getUsername());

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
            return ResponseEntity.ok(new AuthResponseDto(token));
        }
        return ResponseEntity.status(401).build();
    }
}