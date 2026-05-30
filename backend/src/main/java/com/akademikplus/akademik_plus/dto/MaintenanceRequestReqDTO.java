package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.entity.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "MaintenanceRequest request payload")
public class MaintenanceRequestReqDTO {
    private Long roomId;
    private String category;
    private String priority;
    private String description;
    private LocalDate requestDate;
    private String photoUrl;
}
