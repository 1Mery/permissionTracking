package com.hospital.permissiontracking.service.impl;

import com.hospital.permissiontracking.dto.user.LoginUserDto;
import com.hospital.permissiontracking.dto.user.RegisterUserDto;
import com.hospital.permissiontracking.dto.user.UserResponse;
import com.hospital.permissiontracking.dto.user.UserSummaryDto;
import com.hospital.permissiontracking.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public UserResponse register(RegisterUserDto registerUserDto) {
        return null;
    }

    @Override
    public UserResponse login(LoginUserDto loginUserDto) {
        return null;
    }

    @Override
    public UserResponse getUserById(Long userId) {
        return null;
    }

    @Override
    public UserSummaryDto getUserSummary(Long userId) {
        return null;
    }
}
