package com.hospital.permissiontracking.repository;

import com.hospital.permissiontracking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    List<User> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String name, String surname);

}
