package com.hospital.permissiontracking.service.impl;

import com.hospital.permissiontracking.dto.user.LoginUserDto;
import com.hospital.permissiontracking.dto.user.RegisterUserDto;
import com.hospital.permissiontracking.dto.user.UserResponse;
import com.hospital.permissiontracking.dto.user.UserSummaryDto;
import com.hospital.permissiontracking.entity.User;
import com.hospital.permissiontracking.entity.enums.UserRole;
import com.hospital.permissiontracking.repository.UserRepository;
import com.hospital.permissiontracking.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
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
        return null;
    }
}
