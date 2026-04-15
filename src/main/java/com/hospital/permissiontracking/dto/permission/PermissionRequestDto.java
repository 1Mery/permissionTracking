package com.hospital.permissiontracking.dto.permission;

import com.hospital.permissiontracking.entity.enums.PermissionType;

import java.time.LocalDate;

public record PermissionRequestDto(
        Long userId,
        LocalDate startDate,
        LocalDate endDate,
        PermissionType permissionType
) {
}
