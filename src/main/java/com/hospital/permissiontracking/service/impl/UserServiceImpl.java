package com.hospital.permissiontracking.service.impl;

import com.hospital.permissiontracking.config.security.JwtService;
import com.hospital.permissiontracking.dto.user.*;
import com.hospital.permissiontracking.entity.Permission;
import com.hospital.permissiontracking.entity.User;
import com.hospital.permissiontracking.entity.enums.PermissionStatus;
import com.hospital.permissiontracking.entity.enums.UserRole;
import com.hospital.permissiontracking.exception.UserNotFoundException;
import com.hospital.permissiontracking.repository.PermissionRepository;
import com.hospital.permissiontracking.repository.UserRepository;
import com.hospital.permissiontracking.service.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository repository, PermissionRepository permissionRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserResponse register(RegisterUserDto registerUserDto) {
        boolean exist=repository.existsByEmail(registerUserDto.email());
        if (exist){
            throw new IllegalArgumentException("Email already exist");
        }


        User user= new User();
        user.setEmail(registerUserDto.email());
        user.setPassword(passwordEncoder.encode(registerUserDto.password()));
        user.setName(registerUserDto.name());
        user.setSurname(registerUserDto.surname());
        user.setDepartment(registerUserDto.department());
        user.setRole(UserRole.ROLE_USER);
        user.setTotalPermissionDays(20);

        User saveUser=repository.save(user);

        UserResponse userResponse=new UserResponse(
                saveUser.getId(),
                saveUser.getEmail(),
                saveUser.getName(),
                saveUser.getSurname(),
                saveUser.getDepartment(),
                saveUser.getTotalPermissionDays(),
                saveUser.getRole()

        );

        return userResponse;
    }

    @Override
    public LoginResponseDto login(LoginUserDto loginUserDto) {
        User user = repository.findByEmail(loginUserDto.email())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(loginUserDto.password(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );


        return new LoginResponseDto(
                token,
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }


    @Override
    public UserResponse getUserById(Long userId) {
        User user=repository.findById(userId).
                orElseThrow(()->new UserNotFoundException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getSurname(),
                user.getDepartment(),
                user.getTotalPermissionDays(),
                user.getRole()
        );
    }

    @Cacheable(value = "userSummaries",key = "#userId")
    @Override
    public UserSummaryDto getUserSummary(Long userId) {
        User user=repository.findById(userId).
                orElseThrow(()->new UserNotFoundException("User not found"));

        List<Permission> permissions= permissionRepository.findByUserIdAndPermissionStatus(userId,PermissionStatus.ONAYLANDI);

        int usedPermissionDays = permissions.stream()
                .mapToInt(permission ->
                        (int) ChronoUnit.DAYS.between(permission.getStartDate(), permission.getEndDate()) + 1
                )
                .sum();

        int remainingDays=user.getTotalPermissionDays()-usedPermissionDays;
        return new UserSummaryDto(
                user.getName(),
                user.getSurname(),
                user.getTotalPermissionDays(),
                usedPermissionDays,
                remainingDays
        );
    }
}
