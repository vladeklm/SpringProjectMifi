package com.example.bookingservice.dto;

public class RoomDto {
    private Long id;
    private String number;
    private Long hotelId;
    private int timesBooked;

    // Конструкторы
    public RoomDto(Long id, String number, Long hotelId, int timesBooked) {
        this.id = id;
        this.number = number;
        this.hotelId = hotelId;
        this.timesBooked = timesBooked;
    }

    public RoomDto() {
    }

    // Геттеры и Сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public int getTimesBooked() {
        return timesBooked;
    }

    public void setTimesBooked(int timesBooked) {
        this.timesBooked = timesBooked;
    }
}