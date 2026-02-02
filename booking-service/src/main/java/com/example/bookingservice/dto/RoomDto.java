package com.example.bookingservice.dto;

public class RoomDto {
    private Long id;
    private String number;
    private Long hotelId;
    private int timesBooked;

    public RoomDto(Long id, String number, Long hotelId, int timesBooked) {
        this.id = id;
        this.number = number;
        this.hotelId = hotelId;
        this.timesBooked = timesBooked;
    }

    public RoomDto() {

    }

    public Long getId() {
        return id;
    }
}