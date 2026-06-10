package com.akademikplus.akademik_plus.mapper;

import com.akademikplus.akademik_plus.dto.RoomRequestDTO;
import com.akademikplus.akademik_plus.dto.RoomResponseDTO;
import com.akademikplus.akademik_plus.entity.Room;
import com.akademikplus.akademik_plus.enums.OccupancyStatus;
import com.akademikplus.akademik_plus.enums.RoomType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RoomMapperTest {

    private final RoomMapper mapper = new RoomMapper();

    @Test
    void toResponse_mapsAllFields() {
        Room room = new Room();
        room.setId(1L);
        room.setRoomNumber("101A");
        room.setRoomType(RoomType.DOUBLE);
        room.setOccupancyStatus(OccupancyStatus.VACANT);
        room.setOccupiedPlaces(0);
        room.setTotalPlaces(2);
        room.setFloorNumber(1);
        room.setRentPrice(new BigDecimal("650.00"));

        RoomResponseDTO dto = mapper.toResponse(room);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRoomNumber()).isEqualTo("101A");
        assertThat(dto.getRoomType()).isEqualTo(RoomType.DOUBLE);
        assertThat(dto.getOccupancyStatus()).isEqualTo(OccupancyStatus.VACANT);
        assertThat(dto.getOccupiedPlaces()).isEqualTo(0);
        assertThat(dto.getTotalPlaces()).isEqualTo(2);
        assertThat(dto.getFloorNumber()).isEqualTo(1);
        assertThat(dto.getRentPrice()).isEqualByComparingTo("650.00");
    }

    @Test
    void toEntity_mapsAllFields() {
        RoomRequestDTO dto = new RoomRequestDTO();
        dto.setRoomNumber("202B");
        dto.setRoomType(RoomType.TRIPLE);
        dto.setTotalPlaces(1);
        dto.setFloorNumber(2);
        dto.setRentPrice(new BigDecimal("500.00"));

        Room room = mapper.toEntity(dto);

        assertThat(room.getRoomNumber()).isEqualTo("202B");
        assertThat(room.getRoomType()).isEqualTo(RoomType.TRIPLE);
        assertThat(room.getTotalPlaces()).isEqualTo(1);
        assertThat(room.getFloorNumber()).isEqualTo(2);
        assertThat(room.getRentPrice()).isEqualByComparingTo("500.00");
    }

    @Test
    void toEntity_doesNotSetOccupancyStatus() {
        RoomRequestDTO dto = new RoomRequestDTO();
        dto.setRoomNumber("303C");
        dto.setRoomType(RoomType.TRIPLE);
        dto.setTotalPlaces(3);
        dto.setFloorNumber(3);
        dto.setRentPrice(new BigDecimal("400.00"));

        Room room = mapper.toEntity(dto);

        assertThat(room.getOccupancyStatus()).isNull();
        assertThat(room.getOccupiedPlaces()).isNull();
    }
}
