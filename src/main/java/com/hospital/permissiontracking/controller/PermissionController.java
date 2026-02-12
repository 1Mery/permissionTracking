package com.hospital.permissiontracking.controller;

import com.hospital.permissiontracking.dto.PermissionRequestDto;
import com.hospital.permissiontracking.dto.PermissionSummaryDto;
import com.hospital.permissiontracking.entity.Permission;
import com.hospital.permissiontracking.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addLeave(@RequestBody PermissionRequestDto dto) {
        permissionService.addLeave(dto);
    }

    @GetMapping("/personel/{personelId}")
    public List<Permission> listByPersonel(@PathVariable Long personelId) {
        return permissionService.getLeavesByPersonel(personelId);
    }

    @GetMapping("/personel/{personelId}/summary")
    public PermissionSummaryDto summary(@PathVariable Long personelId) {
        return permissionService.getPermissionSummary(personelId);
    }
}
