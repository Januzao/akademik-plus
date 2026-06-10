package com.akademikplus.akademik_plus.controller;

import com.akademikplus.akademik_plus.dto.UserRequestDTO;
import com.akademikplus.akademik_plus.dto.UserResponseDTO;
import com.akademikplus.akademik_plus.exception.ErrorResponse;
import com.akademikplus.akademik_plus.service.UserService;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "User management — admin only")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "List of users returned")
    @GetMapping
    public List<UserResponseDTO> getAll() {
        return userService.findAll();
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public UserResponseDTO getById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        return userService.findById(id);
    }

    @Operation(summary = "Create a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO user) {
        return new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
    }

    @Operation(summary = "Update user details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public UserResponseDTO update(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO user) {
        return userService.update(id, user);
    }

    @Operation(summary = "Upload or replace profile photo",
            description = "Accepts JPEG, PNG, WEBP or GIF — max 5 MB")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo uploaded, URL saved to user profile"),
            @ApiResponse(responseCode = "400", description = "Invalid file type or size",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponseDTO> uploadPhoto(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "Image file (JPEG / PNG / WEBP / GIF, max 5 MB)")
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadPhoto(id, file));
    }

    @Operation(summary = "Delete user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "User ID") @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
