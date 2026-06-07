package com.akademikplus.akademik_plus.dto;

import com.akademikplus.akademik_plus.enums.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Room request payload")
public class RoomRequestDTO {
    private String roomNumber;

    @NotNull
    private RoomType roomType;
    private Integer totalPlaces;
    private Integer floorNumber;
}
