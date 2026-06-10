package com.akademikplus.akademik_plus.mapper;

import com.akademikplus.akademik_plus.dto.MaintenanceRequestReqDTO;
import com.akademikplus.akademik_plus.dto.MaintenanceRequestRespDTO;
import com.akademikplus.akademik_plus.entity.MaintenanceRequest;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.MaintenanceCategory;
import com.akademikplus.akademik_plus.enums.MaintenancePriority;
import com.akademikplus.akademik_plus.enums.MaintenanceStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MaintenanceRequestMapperTest {

    private final MaintenanceRequestMapper mapper = new MaintenanceRequestMapper();

    @Test
    void toResponse_mapsAllFields_withRoomAndUser() {
        Room room = new Room();
        room.setRoomNumber("101A");

        User user = new User();
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        user.setPhone("+48500100200");

        MaintenanceRequest entity = new MaintenanceRequest();
        entity.setId(1L);
        entity.setCategory(MaintenanceCategory.PLUMBING);
        entity.setPriority(MaintenancePriority.HIGH);
        entity.setStatus(MaintenanceStatus.PENDING);
        entity.setDescription("Leaking pipe");
        entity.setRequestDate(LocalDate.of(2024, 11, 1));
        entity.setPhotoUrl("/uploads/maintenance/test.jpg");
        entity.setRoom(room);
        entity.setUser(user);

        MaintenanceRequestRespDTO dto = mapper.toResponse(entity);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCategory()).isEqualTo(MaintenanceCategory.PLUMBING);
        assertThat(dto.getPriority()).isEqualTo(MaintenancePriority.HIGH);
        assertThat(dto.getStatus()).isEqualTo(MaintenanceStatus.PENDING);
        assertThat(dto.getDescription()).isEqualTo("Leaking pipe");
        assertThat(dto.getPhotoUrl()).isEqualTo("/uploads/maintenance/test.jpg");
        assertThat(dto.getRoomNumber()).isEqualTo("101A");
        assertThat(dto.getTenantName()).isEqualTo("Jan Kowalski");
        assertThat(dto.getTenantPhone()).isEqualTo("+48500100200");
    }

    @Test
    void toResponse_nullRoomAndUser_whenAbsent() {
        MaintenanceRequest entity = new MaintenanceRequest();
        entity.setId(2L);
        entity.setCategory(MaintenanceCategory.ELECTRICAL);
        entity.setPriority(MaintenancePriority.LOW);
        entity.setStatus(MaintenanceStatus.IN_PROGRESS);

        MaintenanceRequestRespDTO dto = mapper.toResponse(entity);

        assertThat(dto.getRoomNumber()).isNull();
        assertThat(dto.getTenantName()).isNull();
        assertThat(dto.getTenantPhone()).isNull();
    }

    @Test
    void toEntity_mapsAllFields() {
        MaintenanceRequestReqDTO dto = new MaintenanceRequestReqDTO();
        dto.setRoomId(3L);
        dto.setCategory(MaintenanceCategory.FURNITURE);
        dto.setPriority(MaintenancePriority.MEDIUM);
        dto.setDescription("Broken chair");
        dto.setPhotoUrl("/uploads/test.jpg");

        MaintenanceRequest entity = mapper.toEntity(dto);

        assertThat(entity.getCategory()).isEqualTo(MaintenanceCategory.FURNITURE);
        assertThat(entity.getPriority()).isEqualTo(MaintenancePriority.MEDIUM);
        assertThat(entity.getDescription()).isEqualTo("Broken chair");
        assertThat(entity.getPhotoUrl()).isEqualTo("/uploads/test.jpg");
    }

    @Test
    void toEntity_doesNotSetStatusOrRoom() {
        MaintenanceRequestReqDTO dto = new MaintenanceRequestReqDTO();
        dto.setCategory(MaintenanceCategory.CLEANING);
        dto.setPriority(MaintenancePriority.URGENT);
        dto.setDescription("Dirty bathroom");

        MaintenanceRequest entity = mapper.toEntity(dto);

        assertThat(entity.getStatus()).isNull();
        assertThat(entity.getRoom()).isNull();
        assertThat(entity.getUser()).isNull();
    }
}
