package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.enums.MaintenanceCategory;
import com.akademikplus.akademik_plus.enums.MaintenancePriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Maintenance request payload")
public class MaintenanceRequestReqDTO {

    @Schema(description = "ID of the room with the issue", example = "3")
    @NotNull(message = "Room ID is required")
    private Long roomId;

    @Schema(description = "Category of the issue", example = "PLUMBING")
    @NotNull(message = "Category is required")
    private MaintenanceCategory category;

    @Schema(description = "Priority level", example = "HIGH")
    @NotNull(message = "Priority is required")
    private MaintenancePriority priority;

    @Schema(description = "Detailed description of the problem", example = "Leaking pipe under the sink")
    @NotBlank(message = "Description is required")
    private String description;

    @Schema(description = "URL of an attached photo (set via /photo upload endpoint)")
    private String photoUrl;
}
