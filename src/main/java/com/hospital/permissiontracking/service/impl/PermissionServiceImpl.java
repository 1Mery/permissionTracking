package com.hospital.permissiontracking.service.impl;

import com.hospital.permissiontracking.dto.permission.PermissionRequestDto;
import com.hospital.permissiontracking.dto.permission.PermissionResponseDto;
import com.hospital.permissiontracking.entity.Permission;
import com.hospital.permissiontracking.entity.User;
import com.hospital.permissiontracking.entity.enums.PermissionStatus;
import com.hospital.permissiontracking.entity.enums.PermissionType;
import com.hospital.permissiontracking.repository.PermissionRepository;
import com.hospital.permissiontracking.repository.UserRepository;
import com.hospital.permissiontracking.service.PermissionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository, UserRepository userRepository) {
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PermissionResponseDto createPermission(PermissionRequestDto requestDto) {
        User user=userRepository.findById(requestDto.userId()).
                orElseThrow(()-> new RuntimeException("User not found"));

        if (requestDto.startDate() == null || requestDto.endDate() == null) {
            throw new RuntimeException("Start date and end date can not be null");
        }

        if (requestDto.endDate().isBefore(requestDto.startDate())) {
            throw new RuntimeException("End date not be before start date");
        }

        if (requestDto.startDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Permission not be previous time");
        }
        if (permissionRepository.existsOverlappingPermissionByUser(requestDto.userId(), requestDto.startDate(),requestDto.endDate(),PermissionStatus.REDDEDİLDİ)){
            throw new RuntimeException("Overlapping permission exists for given dates");
        }

        int dayCount= (int) ChronoUnit.DAYS.between(requestDto.startDate(),requestDto.endDate())+1;

        Permission permission= new Permission();
        permission.setUser(user);
        permission.setStartDate(requestDto.startDate());
        permission.setEndDate(requestDto.endDate());
        permission.setPermissionType(requestDto.permissionType());
        permission.setPermissionStatus(PermissionStatus.BEKLEMEDE);

        Permission saveP=permissionRepository.save(permission);

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
    public List<PermissionResponseDto> getUserPermissionList(Long userId) {
        User user= userRepository.findById(userId).
                orElseThrow(()-> new RuntimeException("User not found"));

        List<Permission> permission=permissionRepository.findByUserId(userId);

        return permission.stream()
                .map(permission1 -> {
                    int dayCount= (int) ChronoUnit.DAYS.between(permission1.getStartDate(),permission1.getEndDate())+1;

                    return new PermissionResponseDto(
                            permission1.getId(),
                            permission1.getStartDate(),
                            permission1.getEndDate(),
                            dayCount,
                            permission1.getPermissionStatus(),
                            permission1.getPermissionType()
                    );
                }) .toList();
    }

    @Override
    public List<PermissionResponseDto> getPendingPermissions() {
        List<Permission> permission=permissionRepository.findByPermissionStatus(PermissionStatus.BEKLEMEDE);

        return permission.stream()
                .map(permission1 -> {
                    int dayCount= (int) ChronoUnit.DAYS.between(permission1.getStartDate(),permission1.getEndDate())+1;

                    return new PermissionResponseDto(
                            permission1.getId(),
                            permission1.getStartDate(),
                            permission1.getEndDate(),
                            dayCount,
                            permission1.getPermissionStatus(),
                            permission1.getPermissionType()
                    );
                }) .toList();
    }

    @Override
    public PermissionResponseDto approvePermission(Long permissionId) {
        Permission permission=permissionRepository.findById(permissionId).
                orElseThrow(()-> new RuntimeException("Permission is not found"));

        if (permission.getPermissionStatus()!=PermissionStatus.BEKLEMEDE){
            throw new RuntimeException("Only pending permissions can be approved");
        }

        User user=permission.getUser();

        List<Permission> approvedPermissions =permissionRepository.findByUserIdAndPermissionStatus(user.getId(),PermissionStatus.ONAYLANDI);

        int usedDays = approvedPermissions.stream()
                .mapToInt(permission1 -> (int) ChronoUnit.DAYS.between(
                        permission1.getStartDate(),
                        permission1.getEndDate()) + 1)
                .sum();

        int newDays = (int) ChronoUnit.DAYS.between(
                permission.getStartDate(),
                permission.getEndDate()) + 1;

        if (usedDays + newDays > user.getTotalPermissionDays()) {
            throw new RuntimeException("Not enough permission days");
        }

        permission.setPermissionStatus(PermissionStatus.ONAYLANDI);
        Permission p=permissionRepository.save(permission);

        return new PermissionResponseDto(
                p.getId(),
                p.getStartDate(),
                p.getEndDate(),
                newDays,
                p.getPermissionStatus(),
                p.getPermissionType()
        );
    }

    @Override
    public PermissionResponseDto rejectPermission(Long permissionId) {
        Permission permission=permissionRepository.findById(permissionId).
                orElseThrow(()-> new RuntimeException("Permission is not found"));

        if (permission.getPermissionStatus()!=PermissionStatus.BEKLEMEDE){
            throw new RuntimeException("Only pending permissions can be rejected");
        }

        int dayCount= (int) ChronoUnit.DAYS.between(permission.getStartDate(),permission.getEndDate())+1;

        permission.setPermissionStatus(PermissionStatus.REDDEDİLDİ);
        Permission p=permissionRepository.save(permission);

        return new PermissionResponseDto(
                p.getId(),
                p.getStartDate(),
                p.getEndDate(),
                dayCount,
                p.getPermissionStatus(),
                p.getPermissionType()
        );

    }
}

