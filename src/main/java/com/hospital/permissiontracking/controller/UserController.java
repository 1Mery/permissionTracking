package com.hospital.permissiontracking.controller;

import com.hospital.permissiontracking.dto.permission.PermissionRequestDto;
import com.hospital.permissiontracking.dto.permission.PermissionResponseDto;
import com.hospital.permissiontracking.dto.user.*;
import com.hospital.permissiontracking.entity.User;
import com.hospital.permissiontracking.entity.enums.UserRole;
import com.hospital.permissiontracking.exception.ForbiddenAccessException;
import com.hospital.permissiontracking.service.PermissionService;
import com.hospital.permissiontracking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "Kullanıcı kayıt, giriş ve izin talebi işlemleri")
public class UserController {

    private final UserService userService;
    private final PermissionService permissionService;

    @Operation(summary = "Kullanıcı kaydı oluşturur", description = "Sisteme yeni bir kullanıcı kaydeder.")
    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        return userService.register(registerUserDto);
    }

    @Operation(summary = "Giriş yapar", description = "Kullanıcı adı ve şifre ile sisteme giriş yapar, JWT token döner.")
    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginUserDto loginUserDto) {
        return userService.login(loginUserDto);
    }

    @Operation(summary = "Kullanıcı bilgilerini getirir", description = "Verilen ID'ye sahip kullanıcının detaylarını getirir. Sadece kullanıcının kendisi veya admin erişebilir.")
    @GetMapping("/{userId}")
    public UserResponse getUserById(@Parameter(description = "Kullanıcı ID'si", required = true) @PathVariable Long userId) {
        ensureSelfOrAdmin(userId);
        return userService.getUserById(userId);
    }

    @Operation(summary = "Oturum açmış kullanıcının bilgilerini getirir", description = "Token üzerinden işlem yapan kullanıcının kendi detaylarını getirir.")
    @GetMapping("/me")
    public UserResponse getMe() {
        return userService.getUserById(currentUser().getId());
    }

    @Operation(summary = "Kullanıcı özetini getirir", description = "Kullanıcının toplam izin hakkı, kalan izin hakkı gibi özet bilgilerini getirir.")
    @GetMapping("/{userId}/summary")
    public UserSummaryDto getUserSummary(@Parameter(description = "Kullanıcı ID'si", required = true) @PathVariable Long userId) {
        ensureSelfOrAdmin(userId);
        return userService.getUserSummary(userId);
    }

    @Operation(summary = "Yeni izin talebi oluşturur", description = "Belirtilen kullanıcı için yeni bir izin talebi oluşturur. İzin geçmiş veya üst üste binen tarihlerde olamaz.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "İzin talebi başarıyla oluşturuldu"),
            @ApiResponse(responseCode = "400", description = "Geçersiz tarih aralığı veya çakışan izin")
    })
    @PostMapping("/{userId}/permissions")
    public PermissionResponseDto createPermission(@Parameter(description = "Kullanıcı ID'si", required = true) @PathVariable Long userId,
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

    @Operation(summary = "Kullanıcının izinlerini listeler", description = "Belirtilen kullanıcının tüm izin taleplerini (bekleyen, onaylanan, reddedilen) sayfalı olarak getirir.")
    @GetMapping("/{userId}/permissions")
    public Page<PermissionResponseDto> getUserPermissionList(@Parameter(description = "Kullanıcı ID'si", required = true) @PathVariable Long userId,
                                                             @Parameter(description = "Sayfalama parametreleri") Pageable pageable) {
        ensureSelfOrAdmin(userId);
        return permissionService.getUserPermissionList(userId, pageable);
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
