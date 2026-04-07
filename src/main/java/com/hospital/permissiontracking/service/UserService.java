package com.hospital.permissiontracking.service;

import com.hospital.permissiontracking.dto.user.LoginUserDto;
import com.hospital.permissiontracking.dto.user.RegisterUserDto;
import com.hospital.permissiontracking.dto.user.UserResponse;
import com.hospital.permissiontracking.dto.user.UserSummaryDto;

public interface UserService {
    UserResponse register(RegisterUserDto registerUserDto);

    UserResponse login(LoginUserDto loginUserDto);

    UserResponse getUserById(Long userId);

    UserSummaryDto getUserSummary(Long userId);
}
