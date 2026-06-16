package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.AdminUserPatchDTO;
import com.akademikplus.akademik_plus.dto.UserRequestDTO;
import com.akademikplus.akademik_plus.dto.UserResponseDTO;
import com.akademikplus.akademik_plus.entity.Bill;
import com.akademikplus.akademik_plus.entity.Payment;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.entity.RoomHistory;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.BillStatus;
import com.akademikplus.akademik_plus.enums.OccupancyStatus;
import com.akademikplus.akademik_plus.enums.PaymentStatus;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.mapper.UserMapper;
import com.akademikplus.akademik_plus.repository.BillRepository;
import com.akademikplus.akademik_plus.repository.PaymentRepository;
import com.akademikplus.akademik_plus.repository.RoomHistoryRepository;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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
    private final BillRepository billRepository;
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

        Room room = null;
        if (userRequestDTO.getRoomId() != null) {
            room = roomRepository.findById(userRequestDTO.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + userRequestDTO.getRoomId()));
            user.setRoom(room);
        }

        User saved = userRepository.save(user);
        log.info("User created id={}, email={}, role={}", saved.getId(), saved.getEmail(), saved.getRole());

        if (room != null && room.getRentPrice() != null) {
            createProRataBill(saved, room);
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

        Room newRoom = null;

        if (roomChanged) {
            LocalDate today = LocalDate.now();

            if (oldRoom != null) {
                int count = Math.max(0, (oldRoom.getOccupiedPlaces() == null ? 0 : oldRoom.getOccupiedPlaces()) - 1);
                oldRoom.setOccupiedPlaces(count);
                oldRoom.setOccupancyStatus(
                        count >= (oldRoom.getTotalPlaces() == null ? 0 : oldRoom.getTotalPlaces())
                                ? OccupancyStatus.FULL : OccupancyStatus.VACANT);
                roomRepository.save(oldRoom);

                Optional<RoomHistory> openHistory = roomHistoryRepository
                        .findTopByUserIdAndCheckOutIsNullOrderByCheckInDesc(user.getId());
                openHistory.ifPresent(h -> {
                    h.setCheckOut(today);
                    roomHistoryRepository.save(h);
                });

                // Adjust billing for the old room before creating the new bill
                if (newRoomId != null) {
                    openHistory.ifPresent(h -> adjustOldRoomBill(user, oldRoom, h, today));
                }
            }

            if (newRoomId != null) {
                newRoom = roomRepository.findById(newRoomId)
                        .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + newRoomId));
                int count = (newRoom.getOccupiedPlaces() == null ? 0 : newRoom.getOccupiedPlaces()) + 1;
                newRoom.setOccupiedPlaces(count);
                newRoom.setOccupancyStatus(
                        count >= (newRoom.getTotalPlaces() == null ? 0 : newRoom.getTotalPlaces())
                                ? OccupancyStatus.FULL : OccupancyStatus.VACANT);
                roomRepository.save(newRoom);
                user.setRoom(newRoom);

                RoomHistory history = new RoomHistory();
                history.setUser(user);
                history.setRoomNumber(newRoom.getRoomNumber());
                history.setFloorNumber(newRoom.getFloorNumber());
                history.setRoomType(newRoom.getRoomType());
                history.setRentPrice(newRoom.getRentPrice());
                history.setCheckIn(today);
                roomHistoryRepository.save(history);
            } else {
                user.setRoom(null);
            }
        }

        User saved = userRepository.save(user);
        log.info("User patched id={}, isActive={}, roomId={}", id, dto.getIsActive(), newRoomId);

        if (roomChanged && newRoom != null && newRoom.getRentPrice() != null) {
            createProRataBill(saved, newRoom);
        }

        return userMapper.toResponse(saved);
    }

    /**
     * When a user moves to a new room, adjusts or cancels the bill for the previous room:
     * - Same day, PENDING  → cancel the old bill
     * - Multi-day, PENDING → shrink the bill to actual days stayed
     * - Same day, PAID     → refund full paid amount to wallet
     * - Multi-day, PAID    → refund (paid − actual cost) to wallet if positive
     */
    private void adjustOldRoomBill(User user, Room oldRoom, RoomHistory history, LocalDate today) {
        LocalDate checkIn = history.getCheckIn();
        long actualDays = ChronoUnit.DAYS.between(checkIn, today);

        Optional<Bill> oldBillOpt = billRepository
                .findTopByUserIdAndIssuedDateOrderByIdDesc(user.getId(), checkIn);

        if (oldBillOpt.isEmpty() || !oldBillOpt.get().getTitle().startsWith("Monthly rent")) return;

        Bill bill = oldBillOpt.get();
        int totalDays = checkIn.lengthOfMonth();
        BigDecimal dailyRate = oldRoom.getRentPrice()
                .divide(BigDecimal.valueOf(totalDays), 10, RoundingMode.HALF_UP);

        if (bill.getStatus() == BillStatus.PENDING) {
            if (actualDays == 0) {
                bill.setStatus(BillStatus.CANCELLED);
                log.info("Bill {} cancelled (same-day move) for userId={}", bill.getId(), user.getId());
            } else {
                BigDecimal actualCost = dailyRate.multiply(BigDecimal.valueOf(actualDays))
                        .setScale(2, RoundingMode.HALF_UP);
                bill.setAmount(actualCost);
                bill.setDescription("Adjusted: " + actualDays + " of " + totalDays
                        + " days in room " + oldRoom.getRoomNumber());
                log.info("Bill {} adjusted to {} ({} days) for userId={}", bill.getId(), actualCost, actualDays, user.getId());
            }
            billRepository.save(bill);

        } else if (bill.getStatus() == BillStatus.PAID) {
            BigDecimal actualCost = dailyRate.multiply(BigDecimal.valueOf(actualDays))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal refund = bill.getAmount().subtract(actualCost);
            if (refund.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
                user.setBalance(currentBalance.add(refund));

                Payment refundRecord = new Payment();
                refundRecord.setUser(user);
                refundRecord.setAmount(refund);
                refundRecord.setPaidFor("Refund: room " + oldRoom.getRoomNumber()
                        + " (" + actualDays + " of " + totalDays + " days used)");
                refundRecord.setPaymentDate(today);
                refundRecord.setStatus(PaymentStatus.REFUNDED);
                paymentRepository.save(refundRecord);

                log.info("Refund {} added to wallet for userId={} (bill={}, actualDays={})",
                        refund, user.getId(), bill.getId(), actualDays);
            }
        }
    }

    private void createProRataBill(User user, Room room) {
        LocalDate today = LocalDate.now();
        int totalDays = today.lengthOfMonth();
        int remainingDays = totalDays - today.getDayOfMonth() + 1;

        BigDecimal dailyRate = room.getRentPrice()
                .divide(BigDecimal.valueOf(totalDays), 10, RoundingMode.HALF_UP);
        BigDecimal proRataAmount = dailyRate.multiply(BigDecimal.valueOf(remainingDays))
                .setScale(2, RoundingMode.HALF_UP);

        Bill bill = new Bill();
        bill.setUser(user);
        bill.setAmount(proRataAmount);
        bill.setTitle("Monthly rent — " + today.getMonth() + " " + today.getYear());
        bill.setDescription("Pro-rata charge: " + remainingDays + " of " + totalDays + " days");
        bill.setIssuedDate(today);
        bill.setDueDate(today.withDayOfMonth(totalDays));
        bill.setStatus(BillStatus.PENDING);
        billRepository.save(bill);
        log.info("Pro-rata bill created for userId={}, amount={} ({}/{} days)",
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
