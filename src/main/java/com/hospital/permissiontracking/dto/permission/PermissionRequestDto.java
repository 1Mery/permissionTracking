package com.hospital.permissiontracking.dto.permission;

import com.hospital.permissiontracking.entity.enums.PermissionType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PermissionRequestDto(
        @NotNull
        Long userId,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate,
        @NotNull
        PermissionType permissionType
) {
}
