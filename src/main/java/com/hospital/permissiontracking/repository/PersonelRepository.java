package com.hospital.permissiontracking.repository;

import com.hospital.permissiontracking.entity.Personel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonelRepository extends JpaRepository<Personel,Long> {
}
