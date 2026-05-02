package com.hospital.permissiontracking.service;

import com.hospital.permissiontracking.dto.permission.PermissionRequestDto;
import com.hospital.permissiontracking.dto.permission.PermissionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PermissionService {

PermissionResponseDto createPermission(PermissionRequestDto requestDto);

Page<PermissionResponseDto> getUserPermissionList(Long userId, Pageable pageable);

Page<PermissionResponseDto> getPendingPermissions(Pageable pageable);

PermissionResponseDto approvePermission(Long permissionId);

PermissionResponseDto rejectPermission(Long permissionId);
}
