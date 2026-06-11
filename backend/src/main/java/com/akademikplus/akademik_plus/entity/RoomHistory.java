package com.akademikplus.akademik_plus.entity;

import com.akademikplus.akademik_plus.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type")
    private RoomType roomType;

    @Column(name = "rent_price", precision = 10, scale = 2)
    private BigDecimal rentPrice;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out")
    private LocalDate checkOut;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
