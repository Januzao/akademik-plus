package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Room history entry")
public class RoomHistoryResponseDTO {

    @Schema(description = "Record ID")
    private Long id;

    @Schema(description = "User ID")
    private Long userId;

    @Schema(description = "Tenant full name")
    private String tenantName;

    @Schema(description = "Room number", example = "102")
    private String roomNumber;

    @Schema(description = "Floor number", example = "1")
    private Integer floorNumber;

    @Schema(description = "Room type", example = "DOUBLE")
    private RoomType roomType;

    @Schema(description = "Monthly rent price", example = "500.00")
    private BigDecimal rentPrice;

    @Schema(description = "Check-in date", example = "2024-09-01")
    private LocalDate checkIn;

    @Schema(description = "Check-out date — null if currently residing", example = "2025-06-30")
    private LocalDate checkOut;
}
