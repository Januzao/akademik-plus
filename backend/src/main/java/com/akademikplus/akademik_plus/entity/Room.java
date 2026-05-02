package com.akademikplus.akademik_plus.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(name = "room_type")
    private String roomType;

    @Column(name = "occupancy_status")
    private String occupancyStatus;

    @Column(name = "occupied_places")
    private Integer occupiedPlaces;

    @Column(name = "total_places")
    private Integer totalPlaces;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Column(name = "rent_price")
    private BigDecimal rentPrice;
}
