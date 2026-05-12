package com.akademikplus.akademik_plus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Room request payload")
public class RoomRequestDTO {
    private String roomNumber;
    private String roomType;
    private Integer totalPlaces;
    private Integer floorNumber;
}
