package com.hospital.permissiontracking.repository;

import com.hospital.permissiontracking.entity.Permission;
import com.hospital.permissiontracking.entity.enums.PermissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission,Long> {

    @Query("select count(p) > 0 from Permission p " +
            "where p.user.id = :userId " +
            "and p.permissionStatus <> :status " +
            "and p.startDate <= :end " +
            "and p.endDate >= :start")
    boolean existsOverlappingPermissionByUser(Long userId, LocalDate start, LocalDate end, PermissionStatus status);

    Page<Permission> findByUserId(Long userId, Pageable pageable);

    Page<Permission> findByPermissionStatus(PermissionStatus permissionStatus, Pageable pageable);

    List<Permission> findByUserIdAndPermissionStatus(Long userId, PermissionStatus permissionStatus);

    @Query("SELECT COALESCE(SUM(p.dayCount), 0) FROM Permission p WHERE p.user.id = :userId AND p.permissionStatus = :status")
    int sumDayCountByUserIdAndStatus(Long userId, PermissionStatus status);
}
