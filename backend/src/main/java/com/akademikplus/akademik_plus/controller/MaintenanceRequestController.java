package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.entity.MaintenanceRequest;
import com.akademikplus.akademik_plus.service.MaintenanceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenence-requests")
@RequiredArgsConstructor
public class MaintenanceRequestController {
    private final MaintenanceRequestService maintenanceRequestService;

    @GetMapping
    public List<MaintenanceRequest> getAll() {
        return maintenanceRequestService.findAll();
    }

    @GetMapping("/{id}")
    public MaintenanceRequest getById(@PathVariable Integer id) {
        return maintenanceRequestService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MaintenanceRequest> create(@RequestBody MaintenanceRequest request) {
        return new ResponseEntity<>(maintenanceRequestService.create(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Integer id) {
        maintenanceRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
