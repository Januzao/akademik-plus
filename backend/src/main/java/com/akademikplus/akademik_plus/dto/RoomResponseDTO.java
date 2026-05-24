package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Room response payload")
public class RoomResponseDTO {
    private Long id;
    private String roomNumber;

    @NotNull
    private RoomType roomType;
    private String occupancyStatus;
    private Integer occupiedPlaces;
    private Integer totalPlaces;
    private Integer floorNumber;
}
