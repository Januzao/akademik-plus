package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.RoomHistoryResponseDTO;
import com.akademikplus.akademik_plus.entity.RoomHistory;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.repository.RoomHistoryRepository;
import com.akademikplus.akademik_plus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomHistoryService {

    private final RoomHistoryRepository roomHistoryRepository;
    private final UserRepository userRepository;

    public List<RoomHistoryResponseDTO> findByCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return roomHistoryRepository.findByUserIdOrderByCheckInDesc(user.getId())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<RoomHistoryResponseDTO> findByUserId(Long userId) {
        return roomHistoryRepository.findByUserIdOrderByCheckInDesc(userId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<RoomHistoryResponseDTO> findAll() {
        return roomHistoryRepository.findAllByOrderByCheckInDesc()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private RoomHistoryResponseDTO toDTO(RoomHistory h) {
        RoomHistoryResponseDTO dto = new RoomHistoryResponseDTO();
        dto.setId(h.getId());
        dto.setUserId(h.getUser().getId());
        String name = (h.getUser().getFirstName() != null ? h.getUser().getFirstName() : "")
                + " " + (h.getUser().getLastName() != null ? h.getUser().getLastName() : "");
        dto.setTenantName(name.trim().isEmpty() ? h.getUser().getEmail() : name.trim());
        dto.setRoomNumber(h.getRoomNumber());
        dto.setFloorNumber(h.getFloorNumber());
        dto.setRoomType(h.getRoomType());
        dto.setRentPrice(h.getRentPrice());
        dto.setCheckIn(h.getCheckIn());
        dto.setCheckOut(h.getCheckOut());
        return dto;
    }
}
