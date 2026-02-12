package com.hospital.permissiontracking.controller.Ui;

import org.springframework.ui.Model;
import com.hospital.permissiontracking.dto.PermissionRequestDto;
import com.hospital.permissiontracking.dto.PermissionSummaryDto;
import com.hospital.permissiontracking.entity.Permission;
import com.hospital.permissiontracking.entity.Personel;
import com.hospital.permissiontracking.entity.enums.PermissionType;
import com.hospital.permissiontracking.repository.PersonelRepository;
import com.hospital.permissiontracking.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ui")
public class UiController {

    private final PersonelRepository personelRepository;
    private final PermissionService permissionService;

    // 1) LISTE + ARAMA
    // /ui/personels
    // /ui/personels?q=meryem
    @GetMapping("/personels")
    public String personelList(@RequestParam(required = false) String q, Model model) {
        List<Personel> personels;

        if (q == null || q.isBlank()) {
            personels = personelRepository.findAll();
        } else {
            personels = personelRepository
                    .findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(q, q);
        }

        model.addAttribute("personels", personels);
        model.addAttribute("q", q);

        return "personel-list";
    }

    // 2) YENİ PERSONEL FORM (GET)
    @GetMapping("/personels/new")
    public String newPersonelForm() {
        return "personel-new";
    }

    // 3) YENİ PERSONEL KAYDET (POST)
    @PostMapping("/personels")
    public String createPersonel(
            @RequestParam String name,
            @RequestParam String surname,
            @RequestParam String department,
            @RequestParam Integer totalLeaveDays,
            RedirectAttributes ra
    ) {
        Personel p = new Personel();
        p.setName(name);
        p.setSurname(surname);
        p.setDepartment(department);
        p.setTotalLeaveDays(totalLeaveDays);

        personelRepository.save(p);

        ra.addFlashAttribute("successMessage", "Personel kaydedildi.");
        return "redirect:/ui/personels";
    }

    // 4) PERSONEL DETAY
    @GetMapping("/personels/{id}")
    public String personelDetail(@PathVariable Long id, Model model) {
        Personel personel = personelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personel bulunamadı"));

        List<Permission> leaves = permissionService.getLeavesByPersonel(id);
        PermissionSummaryDto summary = permissionService.getPermissionSummary(id);

        model.addAttribute("personel", personel);
        model.addAttribute("leaves", leaves);
        model.addAttribute("summary", summary);

        return "personel-detail";
    }

    // 5) YENİ İZİN FORM
    @GetMapping("/permissions/new")
    public String newPermissionForm(@RequestParam Long personelId, Model model) {
        model.addAttribute("personelId", personelId);
        model.addAttribute("permissionTypes", PermissionType.values());
        return "permission-new";
    }

    // 6) YENİ İZİN KAYDET (flash message ile)
    @PostMapping("/permissions")
    public String createPermission(
            @RequestParam Long personelId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam PermissionType permissionType,
            RedirectAttributes ra
    ) {
        try {
            PermissionRequestDto dto = new PermissionRequestDto(
                    personelId,
                    LocalDate.parse(startDate),
                    LocalDate.parse(endDate),
                    permissionType
            );

            permissionService.addLeave(dto);

            ra.addFlashAttribute("successMessage", "İzin kaydedildi.");
            return "redirect:/ui/personels/" + personelId;

        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", mapToFriendlyMessage(e.getMessage()));
            return "redirect:/ui/permissions/new?personelId=" + personelId;
        }
    }

    private String mapToFriendlyMessage(String raw) {
        if (raw == null) return "Bir hata oluştu.";
        String m = raw.toLowerCase();

        if (m.contains("çakış") || m.contains("overlap")) {
            return "Seçilen tarihler mevcut bir izinle çakışıyor.";
        }
        if (m.contains("yetersiz") || m.contains("kalan") || m.contains("insufficient")) {
            return "Kalan izin gün sayısı yetersiz.";
        }
        if (m.contains("start") && m.contains("end")) {
            return "Başlangıç tarihi bitiş tarihinden büyük olamaz.";
        }
        return raw;
    }
}
