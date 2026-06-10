package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.RoomRequestDTO;
import com.akademikplus.akademik_plus.dto.RoomResponseDTO;
import com.akademikplus.akademik_plus.exception.ErrorResponse;
import com.akademikplus.akademik_plus.service.RoomService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Rooms", description = "Room management")
public class RoomController {
    private final RoomService roomService;

    @Operation(summary = "Get all rooms")
    @ApiResponse(responseCode = "200", description = "List of rooms returned")
    @GetMapping
    public List<RoomResponseDTO> getAll() {
        return roomService.findAll();
    }

    @Operation(summary = "Get room by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room found"),
            @ApiResponse(responseCode = "404", description = "Room not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public RoomResponseDTO getById(
            @Parameter(description = "Room ID") @PathVariable Long id) {
        return roomService.findById(id);
    }

    @Operation(summary = "Create a new room", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Room created"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<RoomResponseDTO> create(@Valid @RequestBody RoomRequestDTO room) {
        return new ResponseEntity<>(roomService.create(room), HttpStatus.CREATED);
    }

    @Operation(summary = "Update room details", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room updated"),
            @ApiResponse(responseCode = "400", description = "Cannot reduce capacity below current occupancy",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Room not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public RoomResponseDTO update(
            @Parameter(description = "Room ID") @PathVariable Long id,
            @Valid @RequestBody RoomRequestDTO room) {
        return roomService.update(id, room);
    }

    @Operation(summary = "Delete room", description = "Admin only")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Room deleted"),
            @ApiResponse(responseCode = "404", description = "Room not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Room ID") @PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
