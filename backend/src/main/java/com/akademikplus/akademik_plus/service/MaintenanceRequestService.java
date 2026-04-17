package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.entity.MaintenanceRequest;
import com.akademikplus.akademik_plus.repository.MaintenanceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceRequestService {
    private final MaintenanceRequestRepository maintenanceRequests;

    public List<MaintenanceRequest> findAll() {
        return maintenanceRequests.findAll();
    }

    public MaintenanceRequest findById(Integer id) {
        return maintenanceRequests.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request did not found with id: " + id));
    }

    public MaintenanceRequest create(MaintenanceRequest requests) {
        return maintenanceRequests.save(requests);
    }

    public void delete(Integer id) {
        maintenanceRequests.deleteById(id);
    }
}
