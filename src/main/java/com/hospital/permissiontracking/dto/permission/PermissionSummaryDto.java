package com.hospital.permissiontracking.dto.permission;

import java.time.LocalDate;

public record PermissionSummaryDto(
        Long personelId,
        int totalLeaveDays,
        int totalUsedDays,
        int remainingDays,
        LocalDate nextWorkDate
) {
}
