package com.hospital.permissiontracking.entity;

import com.hospital.permissiontracking.entity.enums.PermissionType;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personel_id", nullable = false)
    private Personel personel;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private int usedDays;

    private PermissionType permissionType;
}
