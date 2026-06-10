package com.akademikplus.akademik_plus.mapper;

import com.akademikplus.akademik_plus.dto.UserRequestDTO;
import com.akademikplus.akademik_plus.dto.UserResponseDTO;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.Role;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toResponse_mapsAllFields() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        user.setEmail("jan@example.com");
        user.setPhone("+48500100200");
        user.setRole(Role.STUDENT);
        user.setIsActive(true);
        user.setBalance(new BigDecimal("100.00"));
        user.setProfilePhoto("/uploads/test.jpg");
        user.setPesel("12345678901");
        user.setCountryOfOrigin("Poland");
        user.setDisability("none");
        user.setPersonalPreferences("quiet room");

        UserResponseDTO dto = mapper.toResponse(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("Jan");
        assertThat(dto.getLastName()).isEqualTo("Kowalski");
        assertThat(dto.getEmail()).isEqualTo("jan@example.com");
        assertThat(dto.getPhone()).isEqualTo("+48500100200");
        assertThat(dto.getRole()).isEqualTo(Role.STUDENT);
        assertThat(dto.getIsActive()).isTrue();
        assertThat(dto.getBalance()).isEqualByComparingTo("100.00");
        assertThat(dto.getProfilePhoto()).isEqualTo("/uploads/test.jpg");
        assertThat(dto.getPesel()).isEqualTo("12345678901");
        assertThat(dto.getCountryOfOrigin()).isEqualTo("Poland");
    }

    @Test
    void toResponse_includesRoomId_whenRoomPresent() {
        Room room = new Room();
        room.setId(5L);
        User user = new User();
        user.setId(1L);
        user.setRoom(room);

        UserResponseDTO dto = mapper.toResponse(user);

        assertThat(dto.getRoomId()).isEqualTo(5L);
    }

    @Test
    void toResponse_nullRoomId_whenNoRoom() {
        User user = new User();
        user.setId(1L);

        UserResponseDTO dto = mapper.toResponse(user);

        assertThat(dto.getRoomId()).isNull();
    }

    @Test
    void toEntity_mapsAllFields() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setEmail("jan@example.com");
        dto.setPassword("secret123");
        dto.setPhone("+48500100200");
        dto.setRole(Role.ADMIN);
        dto.setIsActive(true);
        dto.setPesel("12345678901");
        dto.setCountryOfOrigin("Poland");

        User user = mapper.toEntity(dto);

        assertThat(user.getFirstName()).isEqualTo("Jan");
        assertThat(user.getLastName()).isEqualTo("Kowalski");
        assertThat(user.getEmail()).isEqualTo("jan@example.com");
        assertThat(user.getPasswordHash()).isEqualTo("secret123");
        assertThat(user.getPhone()).isEqualTo("+48500100200");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        assertThat(user.getIsActive()).isTrue();
    }

    @Test
    void toEntity_defaultsIsActiveToTrue_whenNull() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setEmail("jan@example.com");
        dto.setPassword("secret123");
        dto.setIsActive(null);

        User user = mapper.toEntity(dto);

        assertThat(user.getIsActive()).isTrue();
    }
}
