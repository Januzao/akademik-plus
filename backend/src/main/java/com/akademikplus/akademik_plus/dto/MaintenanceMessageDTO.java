package com.akademikplus.akademik_plus.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaintenanceMessageDTO {
    private Long id;
    private String senderName;
    private String senderRole;
    private String text;
    private LocalDateTime createdAt;
}
