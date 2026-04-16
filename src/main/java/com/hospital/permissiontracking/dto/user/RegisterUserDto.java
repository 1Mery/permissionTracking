package com.hospital.permissiontracking.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserDto (
        @NotBlank@Email
        String email,
        @NotBlank
        String password,
        @NotBlank
        String name,
        @NotBlank
        String surname,
        @NotBlank
        String department
){
}
