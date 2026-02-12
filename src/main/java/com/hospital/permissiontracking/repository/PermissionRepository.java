package com.hospital.permissiontracking.repository;

import com.hospital.permissiontracking.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission,Long> {
    // Bir personelin tüm izin kayıtları
    List<Permission> findByPersonelId(Long personelId);

    // Toplam kullanılan izin
    @Query("""
           SELECT COALESCE(SUM(p.usedDays), 0) 
           FROM Permission p
           WHERE p.personel.id = :personelId
           """)
    int getTotalUsedLeave(Long personelId);

    //Çakışmayı önlemek
    @Query("""
        SELECT (COUNT(p) > 0)
        FROM Permission p
        WHERE p.personel.id = :personelId
          AND :startDate <= p.endDate
          AND :endDate >= p.startDate
    """)
    boolean existsOverlappingLeave(Long personelId, LocalDate startDate, LocalDate endDate);

}
