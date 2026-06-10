package com.akademikplus.akademik_plus.entity;

import com.akademikplus.akademik_plus.enums.MaintenanceCategory;
import com.akademikplus.akademik_plus.enums.MaintenancePriority;
import com.akademikplus.akademik_plus.enums.MaintenanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private MaintenanceCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private MaintenancePriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MaintenanceStatus status;

    @Column(name = "request_date")
    private LocalDate requestDate;

    private String description;

    @Column(name = "photo_url")
    private String photoUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
