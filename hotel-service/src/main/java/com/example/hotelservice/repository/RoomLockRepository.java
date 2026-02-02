package com.example.hotelservice.repository;

import com.example.hotelservice.entity.RoomLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface RoomLockRepository extends JpaRepository<RoomLock, Long> {
    Optional<RoomLock> findByRequestId(String requestId);

    // Проверка пересечения дат для конкретной комнаты, исключая текущий запрос (если есть)
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM RoomLock r " +
            "WHERE r.roomId = :roomId " +
            "AND r.startDate < :endDate AND r.endDate > :startDate " +
            "AND (r.requestId IS NULL OR r.requestId != :requestId)")
    boolean existsConflict(@Param("roomId") Long roomId,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate,
                           @Param("requestId") String requestId);
}
