package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.RoomHistoryResponseDTO;
import com.akademikplus.akademik_plus.service.RoomHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/room-history")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Room History", description = "Housing history records")
public class RoomHistoryController {

    private final RoomHistoryService roomHistoryService;

    @Operation(summary = "Get housing history for the current user")
    @GetMapping("/my")
    public List<RoomHistoryResponseDTO> getMy(Principal principal) {
        return roomHistoryService.findByCurrentUser(principal.getName());
    }

    @Operation(summary = "Get all housing history (admin only)")
    @GetMapping
    public List<RoomHistoryResponseDTO> getAll() {
        return roomHistoryService.findAll();
    }

    @Operation(summary = "Get housing history for a specific user (admin only)")
    @GetMapping("/user/{userId}")
    public List<RoomHistoryResponseDTO> getByUser(@PathVariable Long userId) {
        return roomHistoryService.findByUserId(userId);
    }
}
