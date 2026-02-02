package com.example.bookingservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class BookingRequestDto {
    private Long roomId;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in future")
    private LocalDate endDate;

    private boolean autoSelect;

    // Пустой конструктор
    public BookingRequestDto() {
    }

    // Getters
    public Long getRoomId() { return roomId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isAutoSelect() { return autoSelect; }

    // Setters
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setAutoSelect(boolean autoSelect) { this.autoSelect = autoSelect; }
}