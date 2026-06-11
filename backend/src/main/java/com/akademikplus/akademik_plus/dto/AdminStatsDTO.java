package com.akademikplus.akademik_plus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "Admin dashboard statistics")
public class AdminStatsDTO {

    @Schema(description = "Total number of rooms")
    private int totalRooms;

    @Schema(description = "Number of vacant rooms")
    private int vacantRooms;

    @Schema(description = "Number of fully occupied rooms")
    private int fullRooms;

    @Schema(description = "Total places across all rooms")
    private int totalPlaces;

    @Schema(description = "Currently occupied places")
    private int occupiedPlaces;

    @Schema(description = "Currently free places")
    private int freePlaces;

    @Schema(description = "Room count by type (DOUBLE, TRIPLE, QUAD)")
    private Map<String, Integer> roomsByType;

    @Schema(description = "Total number of active students")
    private int activeStudents;

    @Schema(description = "Students without an assigned room")
    private int studentsWithoutRoom;

    @Schema(description = "Students whose balance is below their monthly rent (in arrears)")
    private int studentsInArrears;

    @Schema(description = "Total outstanding debt across all students in arrears")
    private BigDecimal totalArrears;

    @Schema(description = "List of students in arrears")
    private List<ArrearsEntryDTO> arrearsDetails;

    @Data
    @Schema(description = "A single arrears entry")
    public static class ArrearsEntryDTO {
        private Long userId;
        private String name;
        private String email;
        private String roomNumber;
        private BigDecimal balance;
        private BigDecimal monthlyRent;
        private BigDecimal deficit;
    }
}
