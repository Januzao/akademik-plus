package com.akademikplus.akademik_plus.dto;

import lombok.Data;

@Data
public class RoomRequestDTO {
    private String roomNumber;
    private String roomType;
    private Integer totalPlaces;
    private Integer floorNumber;
}
