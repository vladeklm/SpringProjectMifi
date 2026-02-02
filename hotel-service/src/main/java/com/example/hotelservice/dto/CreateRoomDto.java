package com.example.hotelservice.dto;

public class CreateRoomDto {
    private Long hotelId;
    private String number;
    private boolean available;

    public CreateRoomDto() {
    }

    public CreateRoomDto(Long hotelId, String number, boolean available) {
        this.hotelId = hotelId;
        this.number = number;
        this.available = available;
    }

    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}