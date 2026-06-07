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
@Schema(description = "Room request payload")
public class RoomRequestDTO {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @NotNull(message = "Total places is required")
    @Min(value = 1, message = "Total places must be at least 1")
    private Integer totalPlaces;

    @NotNull(message = "Floor number is required")
    private Integer floorNumber;

    @NotNull(message = "Rent price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rent price must be greater than 0")
    private BigDecimal rentPrice;
}
