package com.hospital.permissiontracking.controller;

import com.hospital.permissiontracking.dto.permission.PermissionRequestDto;
import com.hospital.permissiontracking.dto.permission.PermissionResponseDto;
import com.hospital.permissiontracking.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService service;

    public PermissionController(PermissionService service) {
        this.service = service;
    }

    @PostMapping
    public PermissionResponseDto createPermission(@RequestBody PermissionRequestDto requestDto){
        return service.createPermission(requestDto);
    }

    @GetMapping("/listPermission/{userId}")
    public List<PermissionResponseDto> getUserPermissionList(@PathVariable Long userId){
        return service.getUserPermissionList(userId);
    }

    @GetMapping("/pendingPermission")
    public List<PermissionResponseDto> getPendingPermissions(){
        return service.getPendingPermissions();
    }

    @PatchMapping("/{permissionId}/approve")
    public PermissionResponseDto approvePermission(@PathVariable Long permissionId) {
        return service.approvePermission(permissionId);
    }

    @PatchMapping("/{permissionId}/reject")
    public PermissionResponseDto rejectPermission(@PathVariable Long permissionId){
        return service.rejectPermission(permissionId);
    }
}
