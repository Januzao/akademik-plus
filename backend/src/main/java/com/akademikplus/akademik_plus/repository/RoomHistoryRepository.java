package com.akademikplus.akademik_plus.repository;

import com.akademikplus.akademik_plus.entity.RoomHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomHistoryRepository extends JpaRepository<RoomHistory, Long> {
    List<RoomHistory> findByUserIdOrderByCheckInDesc(Long userId);
    List<RoomHistory> findAllByOrderByCheckInDesc();
    Optional<RoomHistory> findTopByUserIdAndCheckOutIsNullOrderByCheckInDesc(Long userId);
}
