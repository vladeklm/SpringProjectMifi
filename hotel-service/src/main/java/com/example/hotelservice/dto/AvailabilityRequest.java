package com.example.hotelservice.dto;

import java.time.LocalDate;

public class AvailabilityRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String requestId;

    public AvailabilityRequest() {
    }

    public AvailabilityRequest(LocalDate startDate, LocalDate endDate, String requestId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestId = requestId;
    }

    // --- Getters ---
    public LocalDate getStartDate() {
        return startDate;
    }

    public String getRequestId() {
        return requestId;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    // --- Setters (добавляем то, чего не хватало) ---
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}