package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.MaintenanceRequestReqDTO;
import com.akademikplus.akademik_plus.dto.MaintenanceRequestRespDTO;
import com.akademikplus.akademik_plus.entity.MaintenanceRequest;
import com.akademikplus.akademik_plus.service.MaintenanceRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenence-requests")
@RequiredArgsConstructor
@Tag(name = "MaintenanceRequests", description = "MaintenanceRequest management endpoints")
public class MaintenanceRequestController {
    private final MaintenanceRequestService maintenanceRequestService;

    @GetMapping
    public List<MaintenanceRequest> getAll() {
        return maintenanceRequestService.findAll();
    }

    @GetMapping("/{id}")
    public MaintenanceRequest getById(@PathVariable Long id) {
        return maintenanceRequestService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MaintenanceRequestRespDTO> create(
            @RequestBody MaintenanceRequestReqDTO dto,
            @RequestParam Long userId) {
        return new ResponseEntity<>(
            maintenanceRequestService.createRequest(dto, userId),
            HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MaintenanceRequestRespDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(maintenanceRequestService.updateStatus(id, status));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        maintenanceRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
