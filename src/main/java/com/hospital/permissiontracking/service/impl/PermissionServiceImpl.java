package com.hospital.permissiontracking.service.impl;

import com.hospital.permissiontracking.dto.permission.PermissionRequestDto;
import com.hospital.permissiontracking.dto.permission.PermissionResponseDto;
import com.hospital.permissiontracking.entity.Permission;
import com.hospital.permissiontracking.entity.User;
import com.hospital.permissiontracking.entity.enums.PermissionStatus;
import com.hospital.permissiontracking.exception.InsufficientPermissionDaysException;
import com.hospital.permissiontracking.exception.InvalidDateRangeException;
import com.hospital.permissiontracking.exception.InvalidPermissionStateException;
import com.hospital.permissiontracking.exception.OverlappingPermissionException;
import com.hospital.permissiontracking.exception.PermissionNotFoundException;
import com.hospital.permissiontracking.exception.UserNotFoundException;
import com.hospital.permissiontracking.repository.PermissionRepository;
import com.hospital.permissiontracking.repository.UserRepository;
import com.hospital.permissiontracking.service.PermissionService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    public PermissionServiceImpl(PermissionRepository permissionRepository, UserRepository userRepository, CacheManager cacheManager) {
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }

    @Override
    @Transactional
    public PermissionResponseDto createPermission(PermissionRequestDto requestDto) {
        User user = userRepository.findById(requestDto.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (requestDto.endDate().isBefore(requestDto.startDate())) {
            throw new InvalidDateRangeException("End date cannot be before start date");
        }

        if (requestDto.startDate().isBefore(LocalDate.now())) {
            throw new InvalidDateRangeException("Permission start date cannot be in the past");
        }

        if (permissionRepository.existsOverlappingPermissionByUser(
                requestDto.userId(), requestDto.startDate(), requestDto.endDate(), PermissionStatus.REDDEDİLDİ)) {
            throw new OverlappingPermissionException("Overlapping permission exists for given dates");
        }

        int dayCount = (int) ChronoUnit.DAYS.between(requestDto.startDate(), requestDto.endDate()) + 1;

        Permission permission = new Permission();
        permission.setUser(user);
        permission.setStartDate(requestDto.startDate());
        permission.setEndDate(requestDto.endDate());
        permission.setPermissionType(requestDto.permissionType());
        permission.setPermissionStatus(PermissionStatus.BEKLEMEDE);
        permission.setDayCount(dayCount);

        Permission saveP = permissionRepository.save(permission);

        return new PermissionResponseDto(
                saveP.getId(),
                saveP.getStartDate(),
                saveP.getEndDate(),
                dayCount,
                saveP.getPermissionStatus(),
                saveP.getPermissionType()
        );
    }

    @Override
    public Page<PermissionResponseDto> getUserPermissionList(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        return permissionRepository.findByUserId(userId, pageable)
                .map(this::toResponse);
    }

    @Override
    public Page<PermissionResponseDto> getPendingPermissions(Pageable pageable) {
        return permissionRepository.findByPermissionStatus(PermissionStatus.BEKLEMEDE, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public PermissionResponseDto approvePermission(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new PermissionNotFoundException("Permission is not found"));

        if (permission.getPermissionStatus() != PermissionStatus.BEKLEMEDE) {
            throw new InvalidPermissionStateException("Only pending permissions can be approved");
        }

        User user = permission.getUser();

        int usedDays = permissionRepository.sumDayCountByUserIdAndStatus(user.getId(), PermissionStatus.ONAYLANDI);
        int newDays = permission.getDayCount();

        if (usedDays + newDays > user.getTotalPermissionDays()) {
            throw new InsufficientPermissionDaysException("Not enough permission days");
        }

        permission.setPermissionStatus(PermissionStatus.ONAYLANDI);
        Permission p = permissionRepository.save(permission);

        evictUserSummary(user.getId());

        return toResponse(p);
    }

    @Override
    @Transactional
    public PermissionResponseDto rejectPermission(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new PermissionNotFoundException("Permission is not found"));

        if (permission.getPermissionStatus() != PermissionStatus.BEKLEMEDE) {
            throw new InvalidPermissionStateException("Only pending permissions can be rejected");
        }

        permission.setPermissionStatus(PermissionStatus.REDDEDİLDİ);
        Permission p = permissionRepository.save(permission);

        return toResponse(p);
    }

    /* ----------------- yardımcı metodlar ----------------- */

    private PermissionResponseDto toResponse(Permission p) {
        return new PermissionResponseDto(
                p.getId(),
                p.getStartDate(),
                p.getEndDate(),
                p.getDayCount() != null ? p.getDayCount() : (int) ChronoUnit.DAYS.between(p.getStartDate(), p.getEndDate()) + 1, // Fallback for old data
                p.getPermissionStatus(),
                p.getPermissionType()
        );
    }

    private void evictUserSummary(Long userId) {
        Cache cache = cacheManager.getCache("userSummaries");
        if (cache != null) {
            cache.evict(userId);
        }
    }
}

