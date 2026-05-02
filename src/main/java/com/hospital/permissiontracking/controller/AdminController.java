package com.hospital.permissiontracking.controller;

import com.hospital.permissiontracking.dto.permission.PermissionResponseDto;
import com.hospital.permissiontracking.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin API", description = "Admin yetkisi gerektiren izin yönetim işlemleri")
public class AdminController {

    private final PermissionService permissionService;

    @Operation(summary = "Bekleyen izinleri listeler", description = "Sisteme düşmüş ancak henüz onaylanmamış veya reddedilmemiş izinleri sayfalı olarak döner.")
    @GetMapping("/permissions/pending")
    public Page<PermissionResponseDto> getPendingPermissions(@Parameter(description = "Sayfalama parametreleri") Pageable pageable) {
        return permissionService.getPendingPermissions(pageable);
    }

    @Operation(summary = "İzni onaylar", description = "Verilen ID'ye sahip bekleyen bir izni onaylar. Kullanıcının yeterli izin günü yoksa hata fırlatır.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "İzin başarıyla onaylandı"),
            @ApiResponse(responseCode = "400", description = "İzin beklemede değil veya izin günü yetersiz"),
            @ApiResponse(responseCode = "404", description = "İzin bulunamadı")
    })
    @PatchMapping("/permissions/{permissionId}/approve")
    public PermissionResponseDto approvePermission(@Parameter(description = "Onaylanacak izin ID'si", required = true) @PathVariable Long permissionId) {
        return permissionService.approvePermission(permissionId);
    }

    @Operation(summary = "İzni reddeder", description = "Verilen ID'ye sahip bekleyen bir izni reddeder.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "İzin başarıyla reddedildi"),
            @ApiResponse(responseCode = "400", description = "İzin beklemede değil"),
            @ApiResponse(responseCode = "404", description = "İzin bulunamadı")
    })
    @PatchMapping("/permissions/{permissionId}/reject")
    public PermissionResponseDto rejectPermission(@Parameter(description = "Reddedilecek izin ID'si", required = true) @PathVariable Long permissionId) {
        return permissionService.rejectPermission(permissionId);
    }
}