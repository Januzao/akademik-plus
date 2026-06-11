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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceRequestService {
    private final MaintenanceRequestRepository repository;
    private final MaintenanceRequestMapper mapper;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final FileStorageService fileStorageService;

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

        MaintenanceRequest saved = repository.save(entity);
        log.info("Maintenance request created id={}, userId={}, roomId={}, category={}", saved.getId(), currentUserId, dto.getRoomId(), dto.getCategory());
        return mapper.toResponse(saved);
    }

    public MaintenanceRequestRespDTO uploadPhoto(Long id, MultipartFile file) {
        MaintenanceRequest entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance request not found with id: " + id));

        fileStorageService.delete(entity.getPhotoUrl());
        String url = fileStorageService.store(file, "maintenance");
        entity.setPhotoUrl(url);

        MaintenanceRequest saved = repository.save(entity);
        log.info("Photo uploaded for maintenanceRequestId={}, url={}", id, url);
        return mapper.toResponse(saved);
    }

    public MaintenanceRequestRespDTO updateStatus(Long id, MaintenanceStatus status) {
        MaintenanceRequest entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance request not found with id: " + id));
        MaintenanceStatus previous = entity.getStatus();
        entity.setStatus(status);
        MaintenanceRequest saved = repository.save(entity);
        log.info("Maintenance request id={} status changed: {} -> {}", id, previous, status);
        return mapper.toResponse(saved);
    }

    public List<MaintenanceRequestRespDTO> findByCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return repository.findByUserIdOrderByRequestDateDesc(user.getId())
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public void delete(Long id) {
        MaintenanceRequest entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance request not found with id: " + id));
        fileStorageService.delete(entity.getPhotoUrl());
        repository.delete(entity);
        log.info("Maintenance request deleted id={}", id);
    }
}
