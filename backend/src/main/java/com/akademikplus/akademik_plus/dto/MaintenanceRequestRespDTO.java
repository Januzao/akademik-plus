package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.enums.MaintenanceCategory;
import com.akademikplus.akademik_plus.enums.MaintenancePriority;
import com.akademikplus.akademik_plus.enums.MaintenanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "MaintenanceRequest response payload")
public class MaintenanceRequestRespDTO {
    private Long id;
    private MaintenanceCategory category;
    private MaintenancePriority priority;
    private MaintenanceStatus status;
    private String description;
    private LocalDate requestDate;
    private String photoUrl;

    private String roomNumber;
    private String tenantName;
    private String tenantPhone;
}
