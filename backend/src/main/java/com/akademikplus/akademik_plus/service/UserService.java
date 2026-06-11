package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.AdminUserPatchDTO;
import com.akademikplus.akademik_plus.dto.UserRequestDTO;
import com.akademikplus.akademik_plus.dto.UserResponseDTO;
import com.akademikplus.akademik_plus.entity.Payment;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.OccupancyStatus;
import com.akademikplus.akademik_plus.enums.PaymentStatus;
import com.akademikplus.akademik_plus.enums.Role;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.mapper.UserMapper;
import com.akademikplus.akademik_plus.repository.PaymentRepository;
import com.akademikplus.akademik_plus.repository.RoomRepository;
import com.akademikplus.akademik_plus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final PaymentRepository paymentRepository;

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

        if (saved.getRole() == Role.STUDENT && saved.getRoom() != null && saved.getRoom().getRentPrice() != null) {
            createProRataInvoice(saved);
        }

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
                int count = Math.max(0, (oldRoom.getOccupiedPlaces() == null ? 0 : oldRoom.getOccupiedPlaces()) - 1);
                oldRoom.setOccupiedPlaces(count);
                oldRoom.setOccupancyStatus(
                        count >= (oldRoom.getTotalPlaces() == null ? 0 : oldRoom.getTotalPlaces())
                                ? OccupancyStatus.FULL : OccupancyStatus.VACANT);
                roomRepository.save(oldRoom);
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
            } else {
                user.setRoom(null);
            }
        }

        User saved = userRepository.save(user);
        log.info("User patched id={}, isActive={}, roomId={}", id, dto.getIsActive(), newRoomId);

        if (roomChanged && newRoomId != null && saved.getRole() == Role.STUDENT
                && saved.getRoom() != null && saved.getRoom().getRentPrice() != null) {
            createProRataInvoice(saved);
        }

        return userMapper.toResponse(saved);
    }

    private void createProRataInvoice(User user) {
        LocalDate today = LocalDate.now();
        int totalDays = today.lengthOfMonth();
        int remainingDays = totalDays - today.getDayOfMonth() + 1;

        BigDecimal dailyRate = user.getRoom().getRentPrice()
                .divide(BigDecimal.valueOf(totalDays), 10, RoundingMode.HALF_UP);
        BigDecimal proRataAmount = dailyRate.multiply(BigDecimal.valueOf(remainingDays))
                .setScale(2, RoundingMode.HALF_UP);

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(proRataAmount);
        payment.setPaidFor("Monthly rent — " + today.getMonth() + " " + today.getYear()
                + " (" + remainingDays + "/" + totalDays + " days)");
        payment.setPaymentDate(today);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);
        log.info("Pro-rata invoice created for userId={}, amount={} ({}/{} days)",
                user.getId(), proRataAmount, remainingDays, totalDays);
    }

    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        fileStorageService.delete(user.getProfilePhoto());
        userRepository.delete(user);
        log.info("User deleted id={}", id);
    }
}
