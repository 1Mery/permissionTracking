package com.hospital.permissiontracking.service.impl;

import com.hospital.permissiontracking.dto.PermissionRequestDto;
import com.hospital.permissiontracking.dto.PermissionSummaryDto;
import com.hospital.permissiontracking.entity.Permission;
import com.hospital.permissiontracking.entity.Personel;
import com.hospital.permissiontracking.repository.PermissionRepository;
import com.hospital.permissiontracking.repository.PersonelRepository;
import com.hospital.permissiontracking.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PersonelRepository personelRepository;

    @Override
    @Transactional
    public void addLeave(PermissionRequestDto dto) {

        Personel personel = personelRepository.findById(dto.personelId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personel bulunamadı"));

        LocalDate start = dto.startDate();
        LocalDate end = dto.endDate();

        // 1) Tarih kontrol
        if (start == null || end == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startDate ve endDate zorunlu.");
        }
        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate, startDate'ten önce olamaz.");
        }

        // 2) Çakışma kontrol (repo'da var)
        if (permissionRepository.existsOverlappingLeave(personel.getId(), start, end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu tarih aralığında zaten izin var (çakışıyor).");
        }

        // 3) usedDays hesapla (takvim günü, dahil)
        int usedDays = Math.toIntExact(ChronoUnit.DAYS.between(start, end) + 1);

        // 4) Kalan izin kontrol (repo'da getTotalUsedLeave var)
        int alreadyUsed = permissionRepository.getTotalUsedLeave(personel.getId());
        int remaining = personel.getTotalLeaveDays() - alreadyUsed;

        if (remaining < usedDays) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Yetersiz izin. Kalan: " + remaining + " gün, İstenen: " + usedDays + " gün"
            );
        }

        // 5) Kaydet
        Permission p = new Permission();
        p.setPersonel(personel);
        p.setStartDate(start);
        p.setEndDate(end);
        p.setUsedDays(usedDays);
        p.setPermissionType(dto.permissionType());

        permissionRepository.save(p);
    }

    @Override
    public List<Permission> getLeavesByPersonel(Long personelId) {
        // repo'da var
        List<Permission> leaves = permissionRepository.findByPersonelId(personelId);

        // repo'ya orderBy eklemediysen burada sırala
        leaves.sort(Comparator.comparing(Permission::getStartDate).reversed());
        return leaves;
    }

    @Override
    public PermissionSummaryDto getPermissionSummary(Long personelId) {

        Personel personel = personelRepository.findById(personelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personel bulunamadı"));

        int total = personel.getTotalLeaveDays();
        int used = permissionRepository.getTotalUsedLeave(personelId);
        int remaining = total - used;

        // nextWorkDate: son izne göre endDate + 1
        LocalDate nextWorkDate = null;
        List<Permission> leaves = permissionRepository.findByPersonelId(personelId);
        if (!leaves.isEmpty()) {
            leaves.sort(Comparator.comparing(Permission::getEndDate).reversed());
            nextWorkDate = leaves.get(0).getEndDate().plusDays(1);
        }

        return new PermissionSummaryDto(personelId, total, used, remaining, nextWorkDate);
    }
}

