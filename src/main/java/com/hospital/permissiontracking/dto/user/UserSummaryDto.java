package com.hospital.permissiontracking.dto.user;

public record UserSummaryDto(
        String name,
        int totalPermissionDays,
        int usedPermissionDays,
        int remainingDays
){
}
