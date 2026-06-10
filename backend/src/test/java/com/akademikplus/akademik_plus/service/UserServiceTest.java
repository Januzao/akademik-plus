package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.UserRequestDTO;
import com.akademikplus.akademik_plus.dto.UserResponseDTO;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.Role;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.mapper.UserMapper;
import com.akademikplus.akademik_plus.repository.RoomRepository;
import com.akademikplus.akademik_plus.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private RoomRepository roomRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private FileStorageService fileStorageService;

    @InjectMocks
    private UserService userService;

    private User buildUser(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        user.setEmail(email);
        user.setRole(Role.STUDENT);
        user.setIsActive(true);
        user.setBalance(BigDecimal.ZERO);
        return user;
    }

    private UserResponseDTO buildResponse(Long id, String email) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(id);
        dto.setEmail(email);
        return dto;
    }

    @Test
    void findAll_returnsAllMappedUsers() {
        User user = buildUser(1L, "a@example.com");
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(buildResponse(1L, "a@example.com"));

        List<UserResponseDTO> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("a@example.com");
    }

    @Test
    void findById_returnsUser_whenExists() {
        User user = buildUser(1L, "a@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(buildResponse(1L, "a@example.com"));

        UserResponseDTO result = userService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_throwsNotFound_whenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesUserWithEncodedPassword() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setEmail("jan@example.com");
        dto.setPassword("secret123");
        dto.setRole(Role.STUDENT);

        User entity = buildUser(null, "jan@example.com");
        User saved = buildUser(1L, "jan@example.com");

        when(userMapper.toEntity(dto)).thenReturn(entity);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(buildResponse(1L, "jan@example.com"));

        UserResponseDTO result = userService.create(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(entity.getPasswordHash()).isEqualTo("hashed");
    }

    @Test
    void create_assignsRoom_whenRoomIdProvided() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setEmail("jan@example.com");
        dto.setPassword("secret");
        dto.setRoomId(5L);

        Room room = new Room();
        room.setId(5L);
        User entity = buildUser(null, "jan@example.com");
        User saved = buildUser(1L, "jan@example.com");

        when(userMapper.toEntity(dto)).thenReturn(entity);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(roomRepository.findById(5L)).thenReturn(Optional.of(room));
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(buildResponse(1L, "jan@example.com"));

        userService.create(dto);

        assertThat(entity.getRoom()).isEqualTo(room);
    }

    @Test
    void create_throwsNotFound_whenRoomNotFound() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setEmail("jan@example.com");
        dto.setPassword("secret");
        dto.setRoomId(99L);

        when(userMapper.toEntity(dto)).thenReturn(buildUser(null, "jan@example.com"));
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Room not found");
    }

    @Test
    void update_updatesAndReturnsUser() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstName("Updated");
        dto.setLastName("Name");
        dto.setEmail("updated@example.com");
        dto.setRole(Role.ADMIN);

        User existing = buildUser(1L, "old@example.com");
        User saved = buildUser(1L, "updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(buildResponse(1L, "updated@example.com"));

        UserResponseDTO result = userService.update(1L, dto);

        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void update_encodesPassword_whenProvided() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstName("Jan");
        dto.setLastName("Kowalski");
        dto.setEmail("jan@example.com");
        dto.setPassword("newPassword123");

        User existing = buildUser(1L, "jan@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newHashed");
        when(userRepository.save(any())).thenReturn(existing);
        when(userMapper.toResponse(any())).thenReturn(buildResponse(1L, "jan@example.com"));

        userService.update(1L, dto);

        verify(passwordEncoder).encode("newPassword123");
        assertThat(existing.getPasswordHash()).isEqualTo("newHashed");
    }

    @Test
    void update_throwsNotFound_whenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, new UserRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void uploadPhoto_updatesProfilePhoto() {
        User user = buildUser(1L, "jan@example.com");
        user.setProfilePhoto("/uploads/users/old.jpg");
        User saved = buildUser(1L, "jan@example.com");
        saved.setProfilePhoto("/uploads/users/new.jpg");

        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(fileStorageService.store(file, "users")).thenReturn("/uploads/users/new.jpg");
        when(userRepository.save(user)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(buildResponse(1L, "jan@example.com"));

        userService.uploadPhoto(1L, file);

        verify(fileStorageService).delete("/uploads/users/old.jpg");
        verify(fileStorageService).store(file, "users");
        assertThat(user.getProfilePhoto()).isEqualTo("/uploads/users/new.jpg");
    }

    @Test
    void delete_removesUser() {
        User user = buildUser(1L, "jan@example.com");
        user.setProfilePhoto("/uploads/users/photo.jpg");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(fileStorageService).delete("/uploads/users/photo.jpg");
        verify(userRepository).delete(user);
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
