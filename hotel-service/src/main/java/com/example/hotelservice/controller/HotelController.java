package com.example.hotelservice.controller;

import com.example.hotelservice.dto.AvailabilityRequest;
import com.example.hotelservice.dto.CreateRoomDto;
import com.example.hotelservice.entity.Hotel;
import com.example.hotelservice.entity.Room;
import com.example.hotelservice.repository.HotelRepository;
import com.example.hotelservice.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HotelController {
    private final RoomService roomService;
    private final HotelRepository hotelRepository;

    public HotelController(RoomService roomService, HotelRepository hotelRepository) {
        this.roomService = roomService;
        this.hotelRepository = hotelRepository;
    }

    // --- PUBLIC (USER) ---
    @GetMapping("/rooms/recommend")
    public List<Room> recommend(@RequestParam java.time.LocalDate start, @RequestParam java.time.LocalDate end) {
        return roomService.getAvailableRooms(start, end);
    }

    @GetMapping("/hotels")
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }


    // Меняем возвращаемый тип с Void на Boolean
    @PostMapping("/rooms/{id}/confirm-availability")
    public ResponseEntity<Boolean> confirmAvailability(@PathVariable Long id, @RequestBody AvailabilityRequest req) {
        // Если в RoomService происходит ошибка, она вылетит сюда (500)
        boolean result = roomService.confirmAvailability(id, req.getStartDate(), req.getEndDate(), req.getRequestId());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/rooms/{id}/release")
    public ResponseEntity<Void> release(@PathVariable Long id, @RequestBody AvailabilityRequest req) {
        roomService.releaseRoom(id, req.getRequestId());
        return ResponseEntity.ok().build();
    }

    // --- ADMIN (CRUD) ---
    @PostMapping("/hotels")
    public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel) {
        return ResponseEntity.ok(hotelRepository.save(hotel));
    }

    @PostMapping("/rooms")
    public ResponseEntity<Room> createRoom(@RequestBody CreateRoomDto dto) {
        // Используем новый метод сервиса
        Room room = roomService.saveRoom(dto);
        return ResponseEntity.ok(room);
    }

    @GetMapping("/rooms")
    public List<Room> getAllAvailableRooms(@RequestParam java.time.LocalDate start, @RequestParam java.time.LocalDate end) {
        return roomService.getAllAvailableRooms(start, end);
    }
}