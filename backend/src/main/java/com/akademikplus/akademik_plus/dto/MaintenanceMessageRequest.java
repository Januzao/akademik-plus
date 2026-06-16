package com.akademikplus.akademik_plus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MaintenanceMessageRequest {

    @NotBlank(message = "Message text cannot be blank")
    @Size(max = 2000, message = "Message cannot exceed 2000 characters")
    private String text;
}
