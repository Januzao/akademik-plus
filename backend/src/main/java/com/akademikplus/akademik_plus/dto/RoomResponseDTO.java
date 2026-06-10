package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.enums.OccupancyStatus;
import com.akademikplus.akademik_plus.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Room response payload")
public class RoomResponseDTO {

    @Schema(description = "Room ID", example = "1")
    private Long id;

    @Schema(description = "Room number", example = "101A")
    private String roomNumber;

    @Schema(description = "Room type", example = "DOUBLE")
    @NotNull
    private RoomType roomType;

    @Schema(description = "Occupancy status — VACANT or FULL", example = "VACANT")
    private OccupancyStatus occupancyStatus;

    @Schema(description = "Number of currently occupied beds", example = "1")
    private Integer occupiedPlaces;

    @Schema(description = "Total number of beds", example = "2")
    private Integer totalPlaces;

    @Schema(description = "Floor number", example = "3")
    private Integer floorNumber;

    @Schema(description = "Monthly rent price in PLN", example = "650.00")
    private BigDecimal rentPrice;
}
