package com.akademikplus.akademik_plus.mapper;

import com.akademikplus.akademik_plus.dto.RoomRequestDTO;
import com.akademikplus.akademik_plus.dto.RoomResponseDTO;
import com.akademikplus.akademik_plus.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {
    public RoomResponseDTO toResponse(Room room) {
        RoomResponseDTO dto = new RoomResponseDTO();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRoomType(room.getRoomType());
        dto.setOccupancyStatus(room.getOccupancyStatus());
        dto.setOccupiedPlaces(room.getOccupiedPlaces());
        dto.setTotalPlaces(room.getTotalPlaces());
        dto.setFloorNumber(room.getFloorNumber());
        dto.setRentPrice(room.getRentPrice());
        return dto;
    }

    public Room toEntity(RoomRequestDTO dto) {
        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setRoomType(dto.getRoomType());
        room.setTotalPlaces(dto.getTotalPlaces());
        room.setFloorNumber(dto.getFloorNumber());
        room.setRentPrice(dto.getRentPrice());
        return room;
    }
}
