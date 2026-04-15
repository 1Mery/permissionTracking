package com.hospital.permissiontracking.repository;

import com.hospital.permissiontracking.entity.Permission;
import com.hospital.permissiontracking.entity.enums.PermissionStatus;
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

    List<Permission> findByUserId(Long userId);

    List<Permission> findByPermissionStatus(PermissionStatus permissionStatus);

    List<Permission> findByUserIdAndPermissionStatus(Long userId, PermissionStatus permissionStatus);
}
