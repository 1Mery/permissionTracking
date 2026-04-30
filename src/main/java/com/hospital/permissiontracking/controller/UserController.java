package com.hospital.permissiontracking.controller;

import com.hospital.permissiontracking.dto.permission.PermissionRequestDto;
import com.hospital.permissiontracking.dto.permission.PermissionResponseDto;
import com.hospital.permissiontracking.dto.user.*;
import com.hospital.permissiontracking.entity.User;
import com.hospital.permissiontracking.entity.enums.UserRole;
import com.hospital.permissiontracking.exception.ForbiddenAccessException;
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
        ensureSelfOrAdmin(userId);
        return userService.getUserById(userId);
    }

    //yukardaki endpointin daha güvenli hali. kullanıcı tokendan gelen bilgiyle hareket ediyor. userid direkmen yansıtılmıyor
    @GetMapping("/me")
    public UserResponse getMe() {
        return userService.getUserById(currentUser().getId());
    }

    @GetMapping("/{userId}/summary")
    public UserSummaryDto getUserSummary(@PathVariable Long userId) {
        ensureSelfOrAdmin(userId);
        return userService.getUserSummary(userId);
    }

    @PostMapping("/{userId}/permissions")
    public PermissionResponseDto createPermission(@PathVariable Long userId,
                                                  @Valid @RequestBody PermissionRequestDto requestDto) {
        ensureSelfOrAdmin(userId);

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
        ensureSelfOrAdmin(userId);
        return permissionService.getUserPermissionList(userId);
    }

    /* ----------------- yardımcı metodlar ----------------- */

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new ForbiddenAccessException("Authentication required");
        }
        return user;
    }

    private void ensureSelfOrAdmin(Long targetUserId) {
        User me = currentUser();
        if (me.getRole() == UserRole.ROLE_ADMIN) return;
        if (!me.getId().equals(targetUserId)) {
            throw new ForbiddenAccessException("You are not allowed to access this resource");
        }
    }
}
