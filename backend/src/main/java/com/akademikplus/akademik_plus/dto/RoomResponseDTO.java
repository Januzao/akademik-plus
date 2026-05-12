package com.akademikplus.akademik_plus.dto;

import lombok.Data;

@Data
public class RoomResponseDTO {
    private Long id;
    private String roomNumber;
    private String roomType;
    private String occupancyStatus;
    private Integer occupiedPlaces;
    private Integer totalPlaces;
    private Integer floorNumber;
}
