package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.RoomRequestDTO;
import com.akademikplus.akademik_plus.dto.RoomResponseDTO;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.mapper.RoomMapper;
import com.akademikplus.akademik_plus.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public List<RoomResponseDTO> findAll() {
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toResponse)
                .collect(Collectors.toList());
    }

    public RoomResponseDTO findById(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room did not found with id: " + id));
        return roomMapper.toResponse(room);
    }

    public RoomResponseDTO create(RoomRequestDTO roomRequestDTO) {
        Room room = roomMapper.toEntity(roomRequestDTO);
        room.setOccupiedPlaces(0);
        room.setOccupancyStatus("VACANT");
        Room savedRoom = roomRepository.save(room);
        return roomMapper.toResponse(savedRoom);
    }

    public RoomResponseDTO update(Integer id, RoomRequestDTO roomRequestDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find room with id: " + id));

        if (roomRequestDTO.getTotalPlaces() < room.getOccupiedPlaces()) {
            throw new RuntimeException("Cannot decrease amount of places to: " + roomRequestDTO.getTotalPlaces()
            + ", because in room living " + room.getOccupiedPlaces() + " people.");
        }
        room.setRoomNumber(roomRequestDTO.getRoomNumber());
        room.setRoomType(roomRequestDTO.getRoomType());
        room.setTotalPlaces(roomRequestDTO.getTotalPlaces());
        room.setFloorNumber(roomRequestDTO.getFloorNumber());

        if (room.getOccupiedPlaces() >= room.getTotalPlaces()) {
            room.setOccupancyStatus("FULL");
        } else {
            room.setOccupancyStatus("VACANT");
        }

        Room updatedRoom = roomRepository.save(room);

        return roomMapper.toResponse(updatedRoom);
    }

    public void delete(Integer id) {
        roomRepository.deleteById(id);
    }
}
