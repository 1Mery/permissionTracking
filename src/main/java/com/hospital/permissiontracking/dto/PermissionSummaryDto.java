package com.hospital.permissiontracking.dto;

import java.time.LocalDate;

public record PermissionSummaryDto(
        Long personelId,
        int totalLeaveDays,
        int totalUsedDays,
        int remainingDays,
        LocalDate nextWorkDate
) {
}
