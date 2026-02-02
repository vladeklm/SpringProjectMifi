package com.example.bookingservice.dto;


import java.time.LocalDate;

public class BookingRequestDto {
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean autoSelect;

    public BookingRequestDto(Long roomId, LocalDate startDate, LocalDate endDate, boolean autoSelect) {
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.autoSelect = autoSelect;
    }

    public BookingRequestDto() {    }

    public Long getRoomId() {
        return roomId;
    }

    public boolean isAutoSelect() {
        return autoSelect;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}