package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.MaintenanceRequestReqDTO;
import com.akademikplus.akademik_plus.dto.MaintenanceRequestRespDTO;
import com.akademikplus.akademik_plus.enums.MaintenanceStatus;
import com.akademikplus.akademik_plus.exception.ErrorResponse;
import com.akademikplus.akademik_plus.service.MaintenanceRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Maintenance Requests", description = "Submit and manage maintenance requests")
public class MaintenanceRequestController {
    private final MaintenanceRequestService maintenanceRequestService;

    @Operation(summary = "Get all maintenance requests")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping
    public List<MaintenanceRequestRespDTO> getAll() {
        return maintenanceRequestService.findAll();
    }

    @Operation(summary = "Get maintenance requests for the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "List returned")
    @GetMapping("/my")
    public List<MaintenanceRequestRespDTO> getMy(java.security.Principal principal) {
        return maintenanceRequestService.findByCurrentUser(principal.getName());
    }

    @Operation(summary = "Get maintenance request by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Request found"),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public MaintenanceRequestRespDTO getById(
            @Parameter(description = "Request ID") @PathVariable Long id) {
        return maintenanceRequestService.findById(id);
    }

    @Operation(summary = "Submit a new maintenance request")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Request created with status PENDING"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User or room not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<MaintenanceRequestRespDTO> create(
            @Valid @RequestBody MaintenanceRequestReqDTO dto,
            @Parameter(description = "ID of the user submitting the request") @RequestParam Long userId) {
        return new ResponseEntity<>(
                maintenanceRequestService.createRequest(dto, userId),
                HttpStatus.CREATED);
    }

    @Operation(summary = "Upload or replace photo for a maintenance request",
            description = "Accepts JPEG, PNG, WEBP or GIF — max 5 MB")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo uploaded"),
            @ApiResponse(responseCode = "400", description = "Invalid file type or size",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Request not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MaintenanceRequestRespDTO> uploadPhoto(
            @Parameter(description = "Request ID") @PathVariable Long id,
            @Parameter(description = "Image file (JPEG / PNG / WEBP / GIF, max 5 MB)")
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(maintenanceRequestService.uploadPhoto(id, file));
    }

    @Operation(summary = "Update request status", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "404", description = "Request not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<MaintenanceRequestRespDTO> updateStatus(
            @Parameter(description = "Request ID") @PathVariable Long id,
            @Parameter(description = "New status: PENDING, IN_PROGRESS, RESOLVED, CANCELLED")
            @RequestParam MaintenanceStatus status) {
        return ResponseEntity.ok(maintenanceRequestService.updateStatus(id, status));
    }

    @Operation(summary = "Delete maintenance request", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Request deleted"),
            @ApiResponse(responseCode = "404", description = "Request not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Request ID") @PathVariable Long id) {
        maintenanceRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
