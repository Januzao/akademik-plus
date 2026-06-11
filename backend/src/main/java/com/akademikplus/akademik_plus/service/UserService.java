package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.AdminUserPatchDTO;
import com.akademikplus.akademik_plus.dto.UserRequestDTO;
import com.akademikplus.akademik_plus.dto.UserResponseDTO;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.entity.RoomHistory;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.OccupancyStatus;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.mapper.UserMapper;
import com.akademikplus.akademik_plus.repository.RoomHistoryRepository;
import com.akademikplus.akademik_plus.repository.RoomRepository;
import com.akademikplus.akademik_plus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoomRepository roomRepository;
    private final RoomHistoryRepository roomHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public List<UserResponseDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    public UserResponseDTO create(UserRequestDTO userRequestDTO) {
        User user = userMapper.toEntity(userRequestDTO);
        user.setPasswordHash(passwordEncoder.encode(userRequestDTO.getPassword()));

        if (userRequestDTO.getRoomId() != null) {
            Room room = roomRepository.findById(userRequestDTO.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + userRequestDTO.getRoomId()));
            user.setRoom(room);
        }

        User saved = userRepository.save(user);
        log.info("User created id={}, email={}, role={}", saved.getId(), saved.getEmail(), saved.getRole());
        return userMapper.toResponse(saved);
    }

    public UserResponseDTO update(Long id, UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPhone(userRequestDTO.getPhone());
        user.setRole(userRequestDTO.getRole());
        user.setProfilePhoto(userRequestDTO.getProfilePhoto());

        if (userRequestDTO.getPassword() != null && !userRequestDTO.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(userRequestDTO.getPassword()));
        }

        User updated = userRepository.save(user);
        log.info("User updated id={}, email={}", updated.getId(), updated.getEmail());
        return userMapper.toResponse(updated);
    }

    public UserResponseDTO uploadPhoto(Long id, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        fileStorageService.delete(user.getProfilePhoto());
        String url = fileStorageService.store(file, "users");
        user.setProfilePhoto(url);

        User saved = userRepository.save(user);
        log.info("Profile photo updated for userId={}, url={}", id, url);
        return userMapper.toResponse(saved);
    }

    public UserResponseDTO patch(Long id, AdminUserPatchDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setIsActive(dto.getIsActive());

        Room oldRoom = user.getRoom();
        Long newRoomId = dto.getRoomId();

        boolean roomChanged = (oldRoom == null && newRoomId != null)
                || (oldRoom != null && !oldRoom.getId().equals(newRoomId));

        if (roomChanged) {
            if (oldRoom != null) {
                // decrement old room occupancy
                int count = Math.max(0, (oldRoom.getOccupiedPlaces() == null ? 0 : oldRoom.getOccupiedPlaces()) - 1);
                oldRoom.setOccupiedPlaces(count);
                oldRoom.setOccupancyStatus(
                        count >= (oldRoom.getTotalPlaces() == null ? 0 : oldRoom.getTotalPlaces())
                                ? OccupancyStatus.FULL : OccupancyStatus.VACANT);
                roomRepository.save(oldRoom);

                // close the open history record for old room
                roomHistoryRepository.findTopByUserIdAndCheckOutIsNullOrderByCheckInDesc(user.getId())
                        .ifPresent(h -> {
                            h.setCheckOut(LocalDate.now());
                            roomHistoryRepository.save(h);
                        });
            }

            if (newRoomId != null) {
                Room newRoom = roomRepository.findById(newRoomId)
                        .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + newRoomId));
                int count = (newRoom.getOccupiedPlaces() == null ? 0 : newRoom.getOccupiedPlaces()) + 1;
                newRoom.setOccupiedPlaces(count);
                newRoom.setOccupancyStatus(
                        count >= (newRoom.getTotalPlaces() == null ? 0 : newRoom.getTotalPlaces())
                                ? OccupancyStatus.FULL : OccupancyStatus.VACANT);
                roomRepository.save(newRoom);
                user.setRoom(newRoom);

                // open a new history record for new room
                RoomHistory history = new RoomHistory();
                history.setUser(user);
                history.setRoomNumber(newRoom.getRoomNumber());
                history.setFloorNumber(newRoom.getFloorNumber());
                history.setRoomType(newRoom.getRoomType());
                history.setRentPrice(newRoom.getRentPrice());
                history.setCheckIn(LocalDate.now());
                roomHistoryRepository.save(history);
            } else {
                user.setRoom(null);
            }
        }

        User saved = userRepository.save(user);
        log.info("User patched id={}, isActive={}, roomId={}", id, dto.getIsActive(), newRoomId);
        return userMapper.toResponse(saved);
    }

    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        fileStorageService.delete(user.getProfilePhoto());
        userRepository.delete(user);
        log.info("User deleted id={}", id);
    }
}
