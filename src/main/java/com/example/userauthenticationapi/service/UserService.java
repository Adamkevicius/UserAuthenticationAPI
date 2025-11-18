package com.example.userauthenticationapi.service;

import com.example.userauthenticationapi.dto.request.*;
import com.example.userauthenticationapi.dto.response.DeleteUserResponse;
import com.example.userauthenticationapi.dto.response.UpdateUserResponse;
import com.example.userauthenticationapi.dto.response.UserResponse;
import com.example.userauthenticationapi.exception.ConflictException;
import com.example.userauthenticationapi.exception.ResourceNotFoundException;
import com.example.userauthenticationapi.model.User;
import com.example.userauthenticationapi.model.enums.Role;
import com.example.userauthenticationapi.repo.UserRepo;
import com.example.userauthenticationapi.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public void create(RegisterUserDto registerUserDto) {
        String email = registerUserDto.getEmail();
        String username = registerUserDto.getUsername();

        if (userRepo.existsByEmail(email)) {
            throw new ConflictException("Email is already registered.");
        }

        if (userRepo.existsByUsername(username)) {
            throw new ConflictException("Username already taken.");
        }

        User user = new User();

        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setRole(Role.User);
        user.setAccountVerified(false);

        userRepo.save(user);
    }

    public UserResponse getById(Long id) {
        if (id < 0) {
            throw new ConflictException("Id can not be negative.");
        }

        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));

        return userMapper.toDto(user);
    }

    public UserResponse getByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with username: " + username + " not found.")
                );

        return userMapper.toDto(user);
    }

    public UserResponse getByEmail(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User with username: " + email + " not found.")
        );

        return userMapper.toDto(user);
    }

    public List<UserResponse> getAll() {
        List<User> userList = userRepo.findAll();

        if (userList.isEmpty()) {
            throw new ResourceNotFoundException("Users not found.");
        }

        return userMapper.toListDto(userList);
    }

    public UpdateUserResponse updateById(Long id, UpdateUserDto updateUserDto) {
        isUpdateFieldsValid(
                updateUserDto.getFullName(),
                updateUserDto.getUsername(),
                updateUserDto.getPassword()
        );

        return userRepo.findById(id).map(user -> {
            user.setFullName(updateUserDto.getFullName());
            user.setUsername(updateUserDto.getUsername());
            user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
            userRepo.save(user);

            return userMapper.toUpdateDto(user);
        }).orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));
    }

    public UpdateUserResponse updateByUsername(String username, UpdateUserDto updateUserDto) {
        isUpdateFieldsValid(
                updateUserDto.getFullName(),
                updateUserDto.getUsername(),
                updateUserDto.getPassword()
        );

        return userRepo.findByUsername(username).map(user -> {
            user.setFullName(updateUserDto.getFullName());
            user.setUsername(updateUserDto.getUsername());
            user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
            userRepo.save(user);

            return userMapper.toUpdateDto(user);
        }).orElseThrow(() -> new ResourceNotFoundException("User with username: " + username + " not found."));
    }

    public UpdateUserResponse updateByEmail(String email, UpdateUserDto updateUserDto) {
        isUpdateFieldsValid(
                updateUserDto.getFullName(),
                updateUserDto.getUsername(),
                updateUserDto.getPassword()
        );

        return userRepo.findByEmail(email).map(user -> {
            user.setFullName(updateUserDto.getFullName());
            user.setUsername(updateUserDto.getUsername());
            user.setPassword(passwordEncoder.encode(updateUserDto.getPassword()));
            userRepo.save(user);

            return userMapper.toUpdateDto(user);
        }).orElseThrow(() -> new ResourceNotFoundException("User with email: " + email + " not found."));
    }

    public UpdateUserResponse updatePasswordByEmail(UserPasswordUpdateDto userDto) {
        String email = userDto.getEmail();
        User user = userRepo.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User with email: " + email + " not found.")
        );

        if (userDto.getPassword().isEmpty()) {
            throw new ConflictException("Password field must not be empty.");
        }

        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return userMapper.toUpdateDto(user);
    }

    public DeleteUserResponse deleteById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));

        userRepo.deleteById(id);

        return userMapper.toDeleteUserDto(user, LocalDateTime.now());
    }

    public DeleteUserResponse deleteByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username: " + username + " not found."));

        userRepo.deleteByUsername(username);

        return userMapper.toDeleteUserDto(user, LocalDateTime.now());
    }

    public DeleteUserResponse deleteByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " + email + " not found."));

        userRepo.deleteByEmail(email);

        return userMapper.toDeleteUserDto(user, LocalDateTime.now());
    }

    public void deleteAll() {
        List<User> allUserList = userRepo.findAll();

        if (allUserList.isEmpty()) {
            throw new ResourceNotFoundException("Users not found.");
        }

        userRepo.deleteAllInBatch();
    }

    protected void isUpdateFieldsValid(String fullName, String username, String password) {
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            throw new ResourceNotFoundException("Fields must be not empty.");
        }
    }

}
