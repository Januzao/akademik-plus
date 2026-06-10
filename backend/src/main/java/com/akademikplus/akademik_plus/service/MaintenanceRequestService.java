package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.MaintenanceRequestReqDTO;
import com.akademikplus.akademik_plus.dto.MaintenanceRequestRespDTO;
import com.akademikplus.akademik_plus.entity.MaintenanceRequest;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.MaintenanceStatus;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.mapper.MaintenanceRequestMapper;
import com.akademikplus.akademik_plus.repository.MaintenanceRequestRepository;
import com.akademikplus.akademik_plus.repository.RoomRepository;
import com.akademikplus.akademik_plus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceRequestService {
    private final MaintenanceRequestRepository repository;
    private final MaintenanceRequestMapper mapper;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    public List<MaintenanceRequestRespDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    public MaintenanceRequestRespDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance request not found with id: " + id));
    }

    public MaintenanceRequestRespDTO createRequest(MaintenanceRequestReqDTO dto, Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + dto.getRoomId()));

        MaintenanceRequest entity = mapper.toEntity(dto);
        entity.setRequestDate(LocalDate.now());
        entity.setStatus(MaintenanceStatus.PENDING);
        entity.setUser(user);
        entity.setRoom(room);

        return mapper.toResponse(repository.save(entity));
    }

    public MaintenanceRequestRespDTO updateStatus(Long id, MaintenanceStatus status) {
        MaintenanceRequest entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance request not found with id: " + id));
        entity.setStatus(status);
        return mapper.toResponse(repository.save(entity));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Maintenance request not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
