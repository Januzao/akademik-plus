package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.RoomRequestDTO;
import com.akademikplus.akademik_plus.dto.RoomResponseDTO;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.enums.OccupancyStatus;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.exception.ValidationException;
import com.akademikplus.akademik_plus.mapper.RoomMapper;
import com.akademikplus.akademik_plus.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public List<RoomResponseDTO> findAll() {
        return roomRepository.findAll()
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    public RoomResponseDTO findById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        return roomMapper.toResponse(room);
    }

    public RoomResponseDTO create(RoomRequestDTO roomRequestDTO) {
        Room room = roomMapper.toEntity(roomRequestDTO);
        room.setOccupiedPlaces(0);
        room.setOccupancyStatus(OccupancyStatus.VACANT);
        return roomMapper.toResponse(roomRepository.save(room));
    }

    public RoomResponseDTO update(Long id, RoomRequestDTO roomRequestDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        if (roomRequestDTO.getTotalPlaces() < room.getOccupiedPlaces()) {
            throw new ValidationException(
                "Cannot decrease total places to " + roomRequestDTO.getTotalPlaces()
                + " — room currently has " + room.getOccupiedPlaces() + " occupants.");
        }

        room.setRoomNumber(roomRequestDTO.getRoomNumber());
        room.setRoomType(roomRequestDTO.getRoomType());
        room.setTotalPlaces(roomRequestDTO.getTotalPlaces());
        room.setFloorNumber(roomRequestDTO.getFloorNumber());
        room.setRentPrice(roomRequestDTO.getRentPrice());

        room.setOccupancyStatus(
            room.getOccupiedPlaces() >= room.getTotalPlaces()
                ? OccupancyStatus.FULL
                : OccupancyStatus.VACANT
        );

        return roomMapper.toResponse(roomRepository.save(room));
    }

    public void delete(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new ResourceNotFoundException("Room not found with id: " + id);
        }
        roomRepository.deleteById(id);
    }
}
