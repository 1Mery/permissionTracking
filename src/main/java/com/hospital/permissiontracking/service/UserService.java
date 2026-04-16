package com.hospital.permissiontracking.service;

import com.hospital.permissiontracking.dto.user.*;

public interface UserService {
    UserResponse register(RegisterUserDto registerUserDto);

    LoginResponseDto login(LoginUserDto loginUserDto);

    UserResponse getUserById(Long userId);

    UserSummaryDto getUserSummary(Long userId);
}
