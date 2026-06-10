package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.enums.MaintenanceCategory;
import com.akademikplus.akademik_plus.enums.MaintenancePriority;
import com.akademikplus.akademik_plus.enums.MaintenanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Maintenance request response payload")
public class MaintenanceRequestRespDTO {

    @Schema(description = "Request ID", example = "7")
    private Long id;

    @Schema(description = "Category of the issue", example = "PLUMBING")
    private MaintenanceCategory category;

    @Schema(description = "Priority level", example = "HIGH")
    private MaintenancePriority priority;

    @Schema(description = "Current status", example = "IN_PROGRESS")
    private MaintenanceStatus status;

    @Schema(description = "Problem description", example = "Leaking pipe under the sink")
    private String description;

    @Schema(description = "Date the request was submitted", example = "2024-11-05")
    private LocalDate requestDate;

    @Schema(description = "URL to the attached photo", example = "/uploads/maintenance/abc123.jpg")
    private String photoUrl;

    @Schema(description = "Room number where the issue occurred", example = "101A")
    private String roomNumber;

    @Schema(description = "Full name of the tenant who submitted the request", example = "Jan Kowalski")
    private String tenantName;

    @Schema(description = "Phone number of the tenant", example = "+48 500 100 200")
    private String tenantPhone;
}
