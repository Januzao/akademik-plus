package com.akademikplus.akademik_plus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Request payload for creating a bill")
public class BillCreateDTO {

    @Schema(description = "ID of the user to issue the bill to", example = "5")
    private Long userId;

    @Schema(description = "Bill title", example = "November rent")
    private String title;

    @Schema(description = "Optional description", example = "Room 305, November 2024")
    private String description;

    @Schema(description = "Amount in PLN", example = "850.00")
    private BigDecimal amount;

    @Schema(description = "Due date for payment", example = "2024-11-15")
    private LocalDate dueDate;
}
