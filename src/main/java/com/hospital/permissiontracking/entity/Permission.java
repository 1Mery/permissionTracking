package com.hospital.permissiontracking.entity;

import com.hospital.permissiontracking.entity.enums.PermissionStatus;
import com.hospital.permissiontracking.entity.enums.PermissionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private PermissionType permissionType;

    @Enumerated(EnumType.STRING)
    private PermissionStatus permissionStatus;

    private LocalDate createDate;
}
