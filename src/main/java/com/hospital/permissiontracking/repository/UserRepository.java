package com.hospital.permissiontracking.repository;

import com.hospital.permissiontracking.entity.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    List<User> findByNameContainingIgnoreCaseOrSurnameContainingIgnoreCase(String name, String surname);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(@Email String email);
}
