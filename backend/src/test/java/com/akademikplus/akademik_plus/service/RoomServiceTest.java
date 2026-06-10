package com.akademikplus.akademik_plus.service;

import com.akademikplus.akademik_plus.dto.RoomRequestDTO;
import com.akademikplus.akademik_plus.dto.RoomResponseDTO;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.enums.OccupancyStatus;
import com.akademikplus.akademik_plus.enums.RoomType;
import com.akademikplus.akademik_plus.exception.ResourceNotFoundException;
import com.akademikplus.akademik_plus.exception.ValidationException;
import com.akademikplus.akademik_plus.mapper.RoomMapper;
import com.akademikplus.akademik_plus.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock private RoomRepository roomRepository;
    @Mock private RoomMapper roomMapper;

    @InjectMocks
    private RoomService roomService;

    private Room buildRoom(Long id, int total, int occupied) {
        Room room = new Room();
        room.setId(id);
        room.setRoomNumber("101A");
        room.setRoomType(RoomType.DOUBLE);
        room.setTotalPlaces(total);
        room.setOccupiedPlaces(occupied);
        room.setFloorNumber(1);
        room.setRentPrice(new BigDecimal("650.00"));
        room.setOccupancyStatus(occupied >= total ? OccupancyStatus.FULL : OccupancyStatus.VACANT);
        return room;
    }

    private RoomResponseDTO buildResponse(Long id) {
        RoomResponseDTO dto = new RoomResponseDTO();
        dto.setId(id);
        return dto;
    }

    @Test
    void findAll_returnsAllMappedRooms() {
        Room room = buildRoom(1L, 2, 0);
        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(roomMapper.toResponse(room)).thenReturn(buildResponse(1L));

        List<RoomResponseDTO> result = roomService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void findById_returnsRoom_whenExists() {
        Room room = buildRoom(1L, 2, 0);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomMapper.toResponse(room)).thenReturn(buildResponse(1L));

        RoomResponseDTO result = roomService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_throwsNotFound_whenMissing() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_setsVacantStatusAndZeroOccupied() {
        RoomRequestDTO dto = new RoomRequestDTO();
        dto.setRoomNumber("202B");
        dto.setRoomType(RoomType.DOUBLE);
        dto.setTotalPlaces(1);
        dto.setFloorNumber(2);
        dto.setRentPrice(new BigDecimal("500.00"));

        Room entity = buildRoom(null, 1, 0);
        Room saved = buildRoom(1L, 1, 0);

        when(roomMapper.toEntity(dto)).thenReturn(entity);
        when(roomRepository.save(entity)).thenReturn(saved);
        when(roomMapper.toResponse(saved)).thenReturn(buildResponse(1L));

        roomService.create(dto);

        assertThat(entity.getOccupiedPlaces()).isEqualTo(0);
        assertThat(entity.getOccupancyStatus()).isEqualTo(OccupancyStatus.VACANT);
    }

    @Test
    void update_throwsValidation_whenCapacityReducedBelowOccupied() {
        Room existing = buildRoom(1L, 2, 2);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(existing));

        RoomRequestDTO dto = new RoomRequestDTO();
        dto.setRoomNumber("101A");
        dto.setRoomType(RoomType.DOUBLE);
        dto.setTotalPlaces(1);
        dto.setFloorNumber(1);
        dto.setRentPrice(new BigDecimal("500.00"));

        assertThatThrownBy(() -> roomService.update(1L, dto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Cannot decrease total places");
    }

    @Test
    void update_setsFullStatus_whenAtCapacity() {
        Room existing = buildRoom(1L, 2, 2);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(existing));

        RoomRequestDTO dto = new RoomRequestDTO();
        dto.setRoomNumber("101A");
        dto.setRoomType(RoomType.DOUBLE);
        dto.setTotalPlaces(2);
        dto.setFloorNumber(1);
        dto.setRentPrice(new BigDecimal("650.00"));

        Room saved = buildRoom(1L, 2, 2);
        saved.setOccupancyStatus(OccupancyStatus.FULL);
        when(roomRepository.save(existing)).thenReturn(saved);
        when(roomMapper.toResponse(saved)).thenReturn(buildResponse(1L));

        roomService.update(1L, dto);

        assertThat(existing.getOccupancyStatus()).isEqualTo(OccupancyStatus.FULL);
    }

    @Test
    void update_setsVacantStatus_whenBelowCapacity() {
        Room existing = buildRoom(1L, 2, 1);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(existing));

        RoomRequestDTO dto = new RoomRequestDTO();
        dto.setRoomNumber("101A");
        dto.setRoomType(RoomType.DOUBLE);
        dto.setTotalPlaces(3);
        dto.setFloorNumber(1);
        dto.setRentPrice(new BigDecimal("650.00"));

        Room saved = buildRoom(1L, 3, 1);
        saved.setOccupancyStatus(OccupancyStatus.VACANT);
        when(roomRepository.save(existing)).thenReturn(saved);
        when(roomMapper.toResponse(saved)).thenReturn(buildResponse(1L));

        roomService.update(1L, dto);

        assertThat(existing.getOccupancyStatus()).isEqualTo(OccupancyStatus.VACANT);
    }

    @Test
    void update_throwsNotFound_whenMissing() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.update(99L, new RoomRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deletesRoom() {
        when(roomRepository.existsById(1L)).thenReturn(true);

        roomService.delete(1L);

        verify(roomRepository).deleteById(1L);
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(roomRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> roomService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
