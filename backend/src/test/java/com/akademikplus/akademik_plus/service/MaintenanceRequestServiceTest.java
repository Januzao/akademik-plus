package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.MaintenanceRequestReqDTO;
import com.akademikplus.akademik_plus.dto.MaintenanceRequestRespDTO;
import com.akademikplus.akademik_plus.entity.MaintenanceRequest;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.MaintenanceCategory;
import com.akademikplus.akademik_plus.enums.MaintenancePriority;
import com.akademikplus.akademik_plus.enums.MaintenanceStatus;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.mapper.MaintenanceRequestMapper;
import com.akademikplus.akademik_plus.repository.MaintenanceRequestRepository;
import com.akademikplus.akademik_plus.repository.RoomRepository;
import com.akademikplus.akademik_plus.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceRequestServiceTest {

    @Mock private MaintenanceRequestRepository repository;
    @Mock private MaintenanceRequestMapper mapper;
    @Mock private UserRepository userRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private FileStorageService fileStorageService;

    @InjectMocks
    private MaintenanceRequestService service;

    private MaintenanceRequest buildEntity(Long id) {
        MaintenanceRequest entity = new MaintenanceRequest();
        entity.setId(id);
        entity.setCategory(MaintenanceCategory.PLUMBING);
        entity.setPriority(MaintenancePriority.HIGH);
        entity.setStatus(MaintenanceStatus.PENDING);
        entity.setDescription("Leaking pipe");
        entity.setRequestDate(LocalDate.now());
        return entity;
    }

    private MaintenanceRequestRespDTO buildRespDTO(Long id) {
        MaintenanceRequestRespDTO dto = new MaintenanceRequestRespDTO();
        dto.setId(id);
        return dto;
    }

    @Test
    void findAll_returnsAllRequests() {
        MaintenanceRequest entity = buildEntity(1L);
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(buildRespDTO(1L));

        List<MaintenanceRequestRespDTO> result = service.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void findById_returnsRequest_whenExists() {
        MaintenanceRequest entity = buildEntity(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(buildRespDTO(1L));

        MaintenanceRequestRespDTO result = service.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_throwsNotFound_whenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createRequest_savesWithPendingStatusAndToday() {
        User user = new User();
        user.setId(1L);
        Room room = new Room();
        room.setId(2L);

        MaintenanceRequestReqDTO dto = new MaintenanceRequestReqDTO();
        dto.setRoomId(2L);
        dto.setCategory(MaintenanceCategory.ELECTRICAL);
        dto.setPriority(MaintenancePriority.MEDIUM);
        dto.setDescription("Broken outlet");

        MaintenanceRequest entity = buildEntity(null);
        MaintenanceRequest saved = buildEntity(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(2L)).thenReturn(Optional.of(room));
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(buildRespDTO(1L));

        service.createRequest(dto, 1L);

        assertThat(entity.getStatus()).isEqualTo(MaintenanceStatus.PENDING);
        assertThat(entity.getRequestDate()).isEqualTo(LocalDate.now());
        assertThat(entity.getUser()).isEqualTo(user);
        assertThat(entity.getRoom()).isEqualTo(room);
    }

    @Test
    void createRequest_throwsNotFound_whenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createRequest(new MaintenanceRequestReqDTO(), 99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void createRequest_throwsNotFound_whenRoomMissing() {
        User user = new User();
        user.setId(1L);
        MaintenanceRequestReqDTO dto = new MaintenanceRequestReqDTO();
        dto.setRoomId(99L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createRequest(dto, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Room not found");
    }

    @Test
    void uploadPhoto_updatesPhotoUrl() {
        MaintenanceRequest entity = buildEntity(1L);
        entity.setPhotoUrl("/uploads/old.jpg");
        MaintenanceRequest saved = buildEntity(1L);
        saved.setPhotoUrl("/uploads/new.jpg");

        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[]{1});

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(fileStorageService.store(file, "maintenance")).thenReturn("/uploads/new.jpg");
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(buildRespDTO(1L));

        service.uploadPhoto(1L, file);

        verify(fileStorageService).delete("/uploads/old.jpg");
        assertThat(entity.getPhotoUrl()).isEqualTo("/uploads/new.jpg");
    }

    @Test
    void updateStatus_changesStatus() {
        MaintenanceRequest entity = buildEntity(1L);
        MaintenanceRequest saved = buildEntity(1L);
        saved.setStatus(MaintenanceStatus.IN_PROGRESS);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(buildRespDTO(1L));

        service.updateStatus(1L, MaintenanceStatus.IN_PROGRESS);

        assertThat(entity.getStatus()).isEqualTo(MaintenanceStatus.IN_PROGRESS);
    }

    @Test
    void updateStatus_throwsNotFound_whenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(99L, MaintenanceStatus.RESOLVED))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_removesRequest() {
        MaintenanceRequest entity = buildEntity(1L);
        entity.setPhotoUrl("/uploads/photo.jpg");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(fileStorageService).delete("/uploads/photo.jpg");
        verify(repository).delete(entity);
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
