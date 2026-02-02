package com.example.hotelservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private boolean available; // Операционная доступность
    private int timesBooked; // Статистика

    @ManyToOne
    private Hotel hotel;

    public Room() {
    }

    public Room(String number, boolean available, int timesBooked, Hotel hotel) {
        this.number = number;
        this.available = available;
        this.timesBooked = timesBooked;
        this.hotel = hotel;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public int getTimesBooked() { return timesBooked; }
    public void setTimesBooked(int timesBooked) { this.timesBooked = timesBooked; }

    // ВАЖНЫЙ ГЕТТЕР ДЛЯ СВЯЗИ
    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }
}