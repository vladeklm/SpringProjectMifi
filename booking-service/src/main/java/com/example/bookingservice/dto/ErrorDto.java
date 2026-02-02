package com.example.bookingservice.dto;

import java.time.LocalDateTime;

public class ErrorDto {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String traceId;

    // Пустой конструктор
    public ErrorDto() {
    }

    // Конструктор со всеми полями
    public ErrorDto(int status, String message, LocalDateTime timestamp, String traceId) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.traceId = traceId;
    }

    // Getters
    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getTraceId() { return traceId; }

    // Setters
    public void setStatus(int status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
}