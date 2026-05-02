package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.MaintenanceRequestReqDTO;
import com.akademikplus.akademik_plus.dto.MaintenanceRequestRespDTO;
import com.akademikplus.akademik_plus.entity.MaintenanceRequest;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.entity.User;
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

    public List<MaintenanceRequest> findAll() {
        return repository.findAll();
    }

    public MaintenanceRequest findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request did not found with id: " + id));
    }

    public MaintenanceRequest create(MaintenanceRequest requests) {
        return repository.save(requests);
    }

    public MaintenanceRequestRespDTO createRequest(MaintenanceRequestReqDTO dto, Long currentUserId) {
        MaintenanceRequest entity = mapper.toEntity(dto);

        entity.setRequestDate(LocalDate.now());
        entity.setStatus("Pending");

        User user = userRepository.findById(currentUserId).orElseThrow();
        Room room = roomRepository.findById(dto.getRoomId()).orElseThrow();
        entity.setUser(user);
        entity.setRoom(room);

        MaintenanceRequest saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
