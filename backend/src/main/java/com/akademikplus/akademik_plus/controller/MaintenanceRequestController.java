package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.MaintenanceRequestReqDTO;
import com.akademikplus.akademik_plus.dto.MaintenanceRequestRespDTO;
import com.akademikplus.akademik_plus.enums.MaintenanceStatus;
import com.akademikplus.akademik_plus.service.MaintenanceRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-requests")
@RequiredArgsConstructor
@Tag(name = "MaintenanceRequests", description = "MaintenanceRequest management endpoints")
public class MaintenanceRequestController {
    private final MaintenanceRequestService maintenanceRequestService;

    @GetMapping
    public List<MaintenanceRequestRespDTO> getAll() {
        return maintenanceRequestService.findAll();
    }

    @GetMapping("/{id}")
    public MaintenanceRequestRespDTO getById(@PathVariable Long id) {
        return maintenanceRequestService.findById(id);
    }

    @PostMapping
    public ResponseEntity<MaintenanceRequestRespDTO> create(
            @Valid @RequestBody MaintenanceRequestReqDTO dto,
            @RequestParam Long userId) {
        return new ResponseEntity<>(
                maintenanceRequestService.createRequest(dto, userId),
                HttpStatus.CREATED);
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MaintenanceRequestRespDTO> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(maintenanceRequestService.uploadPhoto(id, file));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<MaintenanceRequestRespDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam MaintenanceStatus status) {
        return ResponseEntity.ok(maintenanceRequestService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        maintenanceRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
