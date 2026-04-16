package com.hospital.permissiontracking.controller;

import com.hospital.permissiontracking.dto.permission.PermissionResponseDto;
import com.hospital.permissiontracking.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PermissionService permissionService;

    @GetMapping("/permissions/pending")
    public List<PermissionResponseDto> getPendingPermissions() {
        return permissionService.getPendingPermissions();
    }

    @PatchMapping("/permissions/{permissionId}/approve")
    public PermissionResponseDto approvePermission(@PathVariable Long permissionId) {
        return permissionService.approvePermission(permissionId);
    }

    @PatchMapping("/permissions/{permissionId}/reject")
    public PermissionResponseDto rejectPermission(@PathVariable Long permissionId) {
        return permissionService.rejectPermission(permissionId);
    }
}