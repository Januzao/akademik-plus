package com.akademikplus.akademik_plus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "MaintenanceRequest request payload")
public class MaintenanceRequestReqDTO {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Priority is required")
    private String priority;

    @NotBlank(message = "Description is required")
    private String description;

    private LocalDate requestDate;
    private String photoUrl;
}
