package com.akademikplus.akademik_plus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Room response payload")
public class RoomResponseDTO {
    private Long id;
    private String roomNumber;
    private String roomType;
    private String occupancyStatus;
    private Integer occupiedPlaces;
    private Integer totalPlaces;
    private Integer floorNumber;
}
