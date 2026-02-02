package com.example.hotelservice.dto;

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

    public AvailabilityRequest() {

    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getRequestId() {
        return requestId;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
