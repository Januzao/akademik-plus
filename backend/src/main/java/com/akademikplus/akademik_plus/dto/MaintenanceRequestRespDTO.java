package com.akademikplus.akademik_plus.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MaintenanceRequestRespDTO {
    private Long id;
    private String category;
    private String priority;
    private String status;
    private String description;
    private LocalDate requestDate;
    private String photoUrl;

    private String roomNumber;
    private String tenantName;
    private String tenantPhone;
}
