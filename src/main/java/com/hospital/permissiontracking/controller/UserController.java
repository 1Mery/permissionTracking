package com.hospital.permissiontracking.controller;

import com.hospital.permissiontracking.dto.permission.PermissionRequestDto;
import com.hospital.permissiontracking.dto.permission.PermissionResponseDto;
import com.hospital.permissiontracking.dto.user.*;
import com.hospital.permissiontracking.entity.User;
import com.hospital.permissiontracking.service.PermissionService;
import com.hospital.permissiontracking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PermissionService permissionService;

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        return userService.register(registerUserDto);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginUserDto loginUserDto) {
        return userService.login(loginUserDto);
    }

    @GetMapping("/{userId}")
    public UserResponse getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    //yukardaki endpointin daha güvenli hali. kullanıcı tokendan gelen bilgiyle hareket ediyor. userid direkmen yansıtılmıyor
    @GetMapping("/me")
    public UserResponse getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return userService.getUserById(user.getId());
    }

    @GetMapping("/{userId}/summary")
    public UserSummaryDto getUserSummary(@PathVariable Long userId) {
        return userService.getUserSummary(userId);
    }

    @PostMapping("/{userId}/permissions")
    public PermissionResponseDto createPermission(@PathVariable Long userId,
                                                  @Valid @RequestBody PermissionRequestDto requestDto) {

        PermissionRequestDto newRequest = new PermissionRequestDto(
                userId,
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.permissionType()
        );

        return permissionService.createPermission(newRequest);
    }

    @GetMapping("/{userId}/permissions")
    public List<PermissionResponseDto> getUserPermissionList(@PathVariable Long userId) {
        return permissionService.getUserPermissionList(userId);
    }
}
