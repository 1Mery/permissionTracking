package com.hospital.permissiontracking.repository;

import com.hospital.permissiontracking.entity.Personel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonelRepository extends JpaRepository<Personel,Long> {
    List<Personel> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String name, String surname);

}
