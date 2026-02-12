package com.hospital.permissiontracking.service;

import com.hospital.permissiontracking.dto.PermissionRequestDto;
import com.hospital.permissiontracking.dto.PermissionSummaryDto;
import com.hospital.permissiontracking.entity.Permission;

import java.util.List;

public interface PermissionService {
    // Yeni izin ekle
    void addLeave(PermissionRequestDto dto);

    // Personelin izinlerini getir
    List<Permission> getLeavesByPersonel(Long personelId);

    // Toplam / kullanılan / kalan izin
    PermissionSummaryDto getPermissionSummary(Long personelId);
}
