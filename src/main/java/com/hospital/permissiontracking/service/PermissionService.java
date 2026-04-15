package com.hospital.permissiontracking.service;

import com.hospital.permissiontracking.dto.permission.PermissionRequestDto;
import com.hospital.permissiontracking.dto.permission.PermissionResponseDto;

import java.util.List;

public interface PermissionService {

PermissionResponseDto createPermission(PermissionRequestDto requestDto);

List<PermissionResponseDto> getUserPermissionList(Long userId);

List<PermissionResponseDto> getPendingPermissions();

PermissionResponseDto approvePermission(Long permissionId);

PermissionResponseDto rejectPermission(Long permissionId);
}
