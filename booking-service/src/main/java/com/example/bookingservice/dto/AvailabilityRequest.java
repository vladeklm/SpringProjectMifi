package com.example.bookingservice.dto;

import java.time.LocalDate;

public class AvailabilityRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private String requestId;

    public AvailabilityRequest(LocalDate startDate, LocalDate endDate, String requestId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.requestId = requestId;
    }

    public AvailabilityRequest() {}

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
}