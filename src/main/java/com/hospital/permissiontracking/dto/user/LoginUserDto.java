package com.hospital.permissiontracking.dto.user;

import jakarta.validation.constraints.Email;

public record LoginUserDto(
        @Email
        String email,
        String password
) {
}
