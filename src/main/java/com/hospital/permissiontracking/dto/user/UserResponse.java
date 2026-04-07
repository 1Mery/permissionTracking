package com.hospital.permissiontracking.dto.user;

import com.hospital.permissiontracking.entity.enums.UserRole;

public record UserResponse(
        Long id,
        String email,
        String name,
        String surname,
        String department,
        int totalPermissionDays,
        UserRole userRole
) {
}
