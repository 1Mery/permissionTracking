package com.hospital.permissiontracking.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserDto (
        @NotBlank @Email
        String email,
        @NotBlank @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
        String password,
        @NotBlank
        String name,
        @NotBlank
        String surname,
        @NotBlank
        String department
){
}
