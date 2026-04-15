package com.hospital.permissiontracking.dto.user;

public record UserSummaryDto(
        String name,
        String surname,
        int totalPermissionDays,
        int usedPermissionDays,
        int remainingDays
){
}
