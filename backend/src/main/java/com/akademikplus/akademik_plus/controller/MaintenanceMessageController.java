package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.MaintenanceMessageDTO;
import com.akademikplus.akademik_plus.dto.MaintenanceMessageRequest;
import com.akademikplus.akademik_plus.service.MaintenanceMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance-requests/{id}/messages")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Maintenance Chat", description = "Per-request message thread between student and admin")
public class MaintenanceMessageController {

    private final MaintenanceMessageService messageService;

    @Operation(summary = "Get all messages for a maintenance request")
    @GetMapping
    public List<MaintenanceMessageDTO> getMessages(
            @PathVariable Long id,
            Principal principal) {
        return messageService.getMessages(id, principal.getName());
    }

    @Operation(summary = "Send a message on a maintenance request")
    @PostMapping
    public ResponseEntity<MaintenanceMessageDTO> sendMessage(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceMessageRequest body,
            Principal principal) {
        MaintenanceMessageDTO dto = messageService.addMessage(id, body.getText(), principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}
