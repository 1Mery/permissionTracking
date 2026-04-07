package com.hospital.permissiontracking.dto.user;

public record RegisterUserDto (
        String email,
        String password,
        String name,
        String surname,
        String department
){
}
