package com.hospital.permissiontracking.dto.permission;

import com.hospital.permissiontracking.entity.enums.PermissionStatus;
import com.hospital.permissiontracking.entity.enums.PermissionType;

import java.time.LocalDate;

public record PermissionResponseDto (
        Long permissionId,
        LocalDate startDate,
        LocalDate endDate,
        int dayCount,
        PermissionStatus permissionStatus,
        PermissionType permissionType
){
}
