package com.example.hotelservice.service;

import com.example.hotelservice.dto.AvailabilityRequest;
import com.example.hotelservice.dto.CreateRoomDto;
import com.example.hotelservice.entity.Room;
import com.example.hotelservice.entity.RoomLock;
import com.example.hotelservice.repository.HotelRepository; // Новый импорт
import com.example.hotelservice.repository.RoomLockRepository;
import com.example.hotelservice.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomLockRepository lockRepository;
    private final HotelRepository hotelRepository; // Новый бин

    // Обновляем конструктор
    public RoomService(RoomRepository roomRepository, RoomLockRepository lockRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.lockRepository = lockRepository;
        this.hotelRepository = hotelRepository;
    }

    // ... остальные методы (confirmAvailability, releaseRoom, getAvailableRooms) остаются без изменений ...
    // ... (копируем их сюда, если вы переписываете файл полностью, иначе просто добавьте метод ниже)

    @Transactional
    public Room saveRoom(CreateRoomDto dto) {
        // Находим отель по ID
        var hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        Room room = new Room();
        room.setHotel(hotel);
        room.setNumber(dto.getNumber());
        room.setAvailable(dto.isAvailable());
        room.setTimesBooked(0); // При создании счетчик 0

        return roomRepository.save(room);
    }

    @Transactional
    public boolean confirmAvailability(Long roomId, LocalDate start, LocalDate end, String requestId) {
        if (lockRepository.findByRequestId(requestId).isPresent()) return true;

        boolean conflict = lockRepository.existsConflict(roomId, start, end, requestId);
        if (conflict) throw new IllegalStateException("Room already booked");

        RoomLock lock = new RoomLock();
        lock.setRoomId(roomId);
        lock.setStartDate(start);
        lock.setEndDate(end);
        lock.setRequestId(requestId);
        lockRepository.save(lock);

        roomRepository.findById(roomId).ifPresent(room -> {
            room.setTimesBooked(room.getTimesBooked() + 1);
            roomRepository.save(room);
        });
        return true;
    }

    @Transactional
    public void releaseRoom(Long roomId, String requestId) {
        lockRepository.findByRequestId(requestId).ifPresent(lock -> {
            roomRepository.findById(roomId).ifPresent(room -> {
                room.setTimesBooked(Math.max(0, room.getTimesBooked() - 1));
                roomRepository.save(room);
            });
            lockRepository.delete(lock);
        });
    }

    public List<Room> getAvailableRooms(LocalDate start, LocalDate end) {
        List<Room> allRooms = roomRepository.findAll();
        return allRooms.stream()
                .filter(Room::isAvailable)
                .filter(room -> !lockRepository.existsConflict(room.getId(), start, end, "dummy-id"))
                .sorted(Comparator.comparingInt(Room::getTimesBooked))
                .toList();
    }


    public List<Room> getAllAvailableRooms(LocalDate start, LocalDate end) {
        List<Room> allRooms = roomRepository.findAll();
        return allRooms.stream()
                .filter(Room::isAvailable)
                // Проверяем отсутствие блокировок (т.е. номер свободен на эти даты)
                .filter(room -> !lockRepository.existsConflict(room.getId(), start, end, "dummy-id"))
                .toList();
    }
}