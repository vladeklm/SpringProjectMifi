package com.example.hotelservice.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class RoomLock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestId; // Корреляционный ID для идемпотентности
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public void setStartDate(LocalDate start) {
        this.startDate = start;
    }

    public void setEndDate(LocalDate end) {
        this.endDate = end;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}