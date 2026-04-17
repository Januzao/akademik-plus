package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public Room findById(Integer id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment did not found with id: " + id));
    }

    public Room create(Room room) {
        return roomRepository.save(room);
    }

    public Room update(Integer id, Room room) {
        Room existing = findById(id);
        existing.setRoomNumber(room.getRoomNumber());
        existing.setRoomType(room.getRoomType());
        existing.setFloorNumber(room.getFloorNumber());
        existing.setOccupiedPlaces(room.getOccupiedPlaces());
        existing.setOccupancyStatus(room.getOccupancyStatus());
        existing.setTotalPlaces(room.getTotalPlaces());
        return roomRepository.save(existing);
    }

    public void delete(Integer id) {
        roomRepository.deleteById(id);
    }
}
