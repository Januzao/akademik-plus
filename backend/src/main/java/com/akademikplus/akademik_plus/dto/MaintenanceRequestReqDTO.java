package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.enums.MaintenanceCategory;
import com.akademikplus.akademik_plus.enums.MaintenancePriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "MaintenanceRequest request payload")
public class MaintenanceRequestReqDTO {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Category is required")
    private MaintenanceCategory category;

    @NotNull(message = "Priority is required")
    private MaintenancePriority priority;

    @NotBlank(message = "Description is required")
    private String description;

    private String photoUrl;
}
