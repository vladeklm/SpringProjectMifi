package com.example.hotelservice;

import com.example.hotelservice.entity.Hotel;
import com.example.hotelservice.entity.Room;
import com.example.hotelservice.repository.HotelRepository;
import com.example.hotelservice.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
public class HotelServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelServiceApplication.class, args);
    }

    // Вложенный класс для инициализации данных
    @Component
    @Transactional // Все операции в одной транзакции
    public class DataLoader implements CommandLineRunner {

        @Autowired
        private HotelRepository hotelRepository;

        @Autowired
        private RoomRepository roomRepository;

        @Override
        public void run(String... args) throws Exception {
            // Проверяем, есть ли уже отели (чтобы не дублировать при перезапуске, хотя H2 и так очищается)
            if (hotelRepository.count() > 0) {
                return;
            }

            // 1. Создаем отель
            Hotel hotel = new Hotel();
            hotel.setName("Grand Hotel");
            hotel.setAddress("Moscow, Red Square");

            hotel = hotelRepository.save(hotel);

            // 2. Создаем номера и привязываем их к отелю
            Room room1 = new Room();
            room1.setNumber("101");
            room1.setAvailable(true);
            room1.setTimesBooked(0);
            room1.setHotel(hotel); // Важно: устанавливаем связь

            Room room2 = new Room();
            room2.setNumber("102");
            room2.setAvailable(true);
            room2.setTimesBooked(0);
            room2.setHotel(hotel); // Важно: устанавливаем связь

            roomRepository.save(room1);
            roomRepository.save(room2);

            System.out.println(">>> Database seeded with 1 Hotel and 2 Rooms.");
        }
    }
}