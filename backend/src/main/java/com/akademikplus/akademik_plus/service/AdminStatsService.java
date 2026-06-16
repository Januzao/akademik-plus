package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.AdminStatsDTO;
import com.akademikplus.akademik_plus.dto.ArrearsEntryDTO;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.entity.User;
import com.akademikplus.akademik_plus.enums.OccupancyStatus;
import com.akademikplus.akademik_plus.enums.Role;
import com.akademikplus.akademik_plus.repository.RoomRepository;
import com.akademikplus.akademik_plus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public AdminStatsDTO getStats() {
        List<Room> rooms = roomRepository.findAll();
        List<User> students = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.STUDENT)
                .toList();

        AdminStatsDTO dto = new AdminStatsDTO();

        dto.setTotalRooms(rooms.size());
        dto.setVacantRooms((int) rooms.stream().filter(r -> r.getOccupancyStatus() == OccupancyStatus.VACANT).count());
        dto.setFullRooms((int) rooms.stream().filter(r -> r.getOccupancyStatus() == OccupancyStatus.FULL).count());

        int totalPlaces = rooms.stream().mapToInt(r -> r.getTotalPlaces() == null ? 0 : r.getTotalPlaces()).sum();
        int occupiedPlaces = rooms.stream().mapToInt(r -> r.getOccupiedPlaces() == null ? 0 : r.getOccupiedPlaces()).sum();
        dto.setTotalPlaces(totalPlaces);
        dto.setOccupiedPlaces(occupiedPlaces);
        dto.setFreePlaces(totalPlaces - occupiedPlaces);

        Map<String, Integer> byType = rooms.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getRoomType() == null ? "UNKNOWN" : r.getRoomType().name(),
                        Collectors.summingInt(r -> 1)
                ));
        dto.setRoomsByType(byType);

        long activeStudents = students.stream().filter(u -> Boolean.TRUE.equals(u.getIsActive())).count();
        dto.setActiveStudents((int) activeStudents);
        dto.setStudentsWithoutRoom((int) students.stream().filter(u -> u.getRoom() == null).count());

        List<ArrearsEntryDTO> arrearsDetails = students.stream()
                .filter(u -> Boolean.TRUE.equals(u.getIsActive())
                        && u.getRoom() != null
                        && u.getRoom().getRentPrice() != null)
                .filter(u -> {
                    BigDecimal balance = u.getBalance() == null ? BigDecimal.ZERO : u.getBalance();
                    return balance.compareTo(u.getRoom().getRentPrice()) < 0;
                })
                .map(u -> {
                    BigDecimal balance = u.getBalance() == null ? BigDecimal.ZERO : u.getBalance();
                    BigDecimal rent = u.getRoom().getRentPrice();
                    return new ArrearsEntryDTO(
                            u.getId(),
                            u.getFirstName() + " " + u.getLastName(),
                            u.getEmail(),
                            u.getRoom().getRoomNumber(),
                            balance,
                            rent,
                            rent.subtract(balance)
                    );
                })
                .toList();

        BigDecimal totalArrears = arrearsDetails.stream()
                .map(ArrearsEntryDTO::getDeficit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setStudentsInArrears(arrearsDetails.size());
        dto.setTotalArrears(totalArrears);
        dto.setArrearsDetails(arrearsDetails);

        return dto;
    }
}
