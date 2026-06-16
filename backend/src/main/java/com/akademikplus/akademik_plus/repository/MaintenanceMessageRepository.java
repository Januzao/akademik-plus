package com.akademikplus.akademik_plus.repository;

import com.akademikplus.akademik_plus.entity.MaintenanceMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceMessageRepository extends JpaRepository<MaintenanceMessage, Long> {
    List<MaintenanceMessage> findByRequestIdOrderByCreatedAtAsc(Long requestId);
}
