package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Room creation / update payload")
public class RoomRequestDTO {

    @Schema(description = "Room number", example = "101A")
    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @Schema(description = "Room type — SINGLE, DOUBLE, TRIPLE, QUAD", example = "DOUBLE")
    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @Schema(description = "Total number of beds", example = "2")
    @NotNull(message = "Total places is required")
    @Min(value = 1, message = "Total places must be at least 1")
    private Integer totalPlaces;

    @Schema(description = "Floor number", example = "3")
    @NotNull(message = "Floor number is required")
    private Integer floorNumber;

    @Schema(description = "Monthly rent price in PLN", example = "650.00")
    @NotNull(message = "Rent price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rent price must be greater than 0")
    private BigDecimal rentPrice;
}
