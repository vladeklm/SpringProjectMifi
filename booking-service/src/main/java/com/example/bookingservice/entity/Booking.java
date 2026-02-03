package com.example.bookingservice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BookingStatus status;
    private String requestId;
    private LocalDateTime createdAt;

    public Booking() { this.createdAt = LocalDateTime.now(); }

    public void setUserId(Long id) {
        this.userId = id;
    }

    public void setRoomId(Long targetRoomId) {
        this.roomId = targetRoomId;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStatus(BookingStatus bookingStatus) {
        this.status = bookingStatus;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getUserId() {
        return userId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setId(long l) {
        this.id = l;
    }
}