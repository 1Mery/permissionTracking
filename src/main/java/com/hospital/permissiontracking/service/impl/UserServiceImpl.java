package com.hospital.permissiontracking.service.impl;

import com.hospital.permissiontracking.dto.user.LoginUserDto;
import com.hospital.permissiontracking.dto.user.RegisterUserDto;
import com.hospital.permissiontracking.dto.user.UserResponse;
import com.hospital.permissiontracking.dto.user.UserSummaryDto;
import com.hospital.permissiontracking.entity.Permission;
import com.hospital.permissiontracking.entity.User;
import com.hospital.permissiontracking.entity.enums.PermissionStatus;
import com.hospital.permissiontracking.entity.enums.UserRole;
import com.hospital.permissiontracking.repository.PermissionRepository;
import com.hospital.permissiontracking.repository.UserRepository;
import com.hospital.permissiontracking.service.UserService;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PermissionRepository permissionRepository;

    public UserServiceImpl(UserRepository repository, PermissionRepository permissionRepository) {
        this.repository = repository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public UserResponse register(RegisterUserDto registerUserDto) {
        boolean exist=repository.existsByEmail(registerUserDto.email());
        if (exist){
            throw new IllegalArgumentException("Email already exist");
        }


        User user= new User();
        user.setEmail(registerUserDto.email());
        user.setPassword(registerUserDto.password());
        user.setName(registerUserDto.name());
        user.setSurname(registerUserDto.surname());
        user.setDepartment(registerUserDto.department());
        user.setRole(UserRole.USER);
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
    public void login(LoginUserDto loginUserDto) {
        User user=repository.findByEmail(loginUserDto.email()).
                orElseThrow(()-> new RuntimeException("User not found"));

        if (!loginUserDto.password().equals(user.getPassword())){
            throw new RuntimeException("Wrong Password");
        }
    }


    @Override
    public UserResponse getUserById(Long userId) {
        User user=repository.findById(userId).
                orElseThrow(()->new RuntimeException("User not found"));

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

    @Override
    public UserSummaryDto getUserSummary(Long userId) {
        User user=repository.findById(userId).
                orElseThrow(()->new RuntimeException("User not found"));

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
