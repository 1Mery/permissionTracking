package com.hospital.permissiontracking.dto;

import com.hospital.permissiontracking.entity.enums.PermissionType;

import java.time.LocalDate;

public record PermissionRequestDto(
        Long personelId,
        LocalDate startDate,
        LocalDate endDate,
        PermissionType permissionType
) {
}
