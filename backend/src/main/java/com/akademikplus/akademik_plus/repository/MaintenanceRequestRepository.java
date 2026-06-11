package com.akademikplus.akademik_plus.repository;

import com.akademikplus.akademik_plus.entity.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByUserIdOrderByRequestDateDesc(Long userId);
}
