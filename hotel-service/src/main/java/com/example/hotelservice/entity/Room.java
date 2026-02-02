package com.example.hotelservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Id;

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

    public int getTimesBooked() {
        return timesBooked;
    }

    public void setTimesBooked(int i) {
        this.timesBooked = i;
    }

    public boolean isAvailable() {
        return available;
    }

    public Long getId() {
        return id;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
