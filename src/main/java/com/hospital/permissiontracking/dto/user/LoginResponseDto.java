package com.hospital.permissiontracking.dto.user;

import com.hospital.permissiontracking.entity.enums.PermissionType;
import com.hospital.permissiontracking.entity.enums.UserRole;

public record LoginResponseDto(
        String token,
        Long userId,
        String email,
        UserRole userRole
) {
}
