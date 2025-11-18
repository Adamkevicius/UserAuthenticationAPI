package com.example.userauthenticationapi.controller;

import com.example.userauthenticationapi.dto.request.RegisterUserDto;
import com.example.userauthenticationapi.dto.request.UpdateUserDto;
import com.example.userauthenticationapi.dto.request.UserPasswordUpdateDto;
import com.example.userauthenticationapi.dto.response.ApiSuccessResponse;
import com.example.userauthenticationapi.dto.response.DeleteUserResponse;
import com.example.userauthenticationapi.dto.response.UpdateUserResponse;
import com.example.userauthenticationapi.dto.response.UserResponse;
import com.example.userauthenticationapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiSuccessResponse> create(@RequestBody RegisterUserDto registerUserDto) {
        userService.create(registerUserDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "User successfully created!",
                                registerUserDto.getUsername(),
                                LocalDateTime.now()
                        )
                );
    }

    @GetMapping("/by-id/{id}")
    public ResponseEntity<ApiSuccessResponse> getById(@PathVariable Long id) {
        UserResponse user = userService.getById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "Success!",
                                user.toString(),
                                LocalDateTime.now()
                        )
                );
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<ApiSuccessResponse> getByUsername(@PathVariable String username) {
        UserResponse user = userService.getByUsername(username);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "Success!",
                                user.toString(),
                                LocalDateTime.now()
                        )
                );
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<ApiSuccessResponse> getByEmail(@PathVariable String email) {
        UserResponse user = userService.getByEmail(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "Success!",
                                user.toString(),
                                LocalDateTime.now()
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiSuccessResponse> getAll() {
        List<UserResponse> userList = userService.getAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "Success!",
                                userList.toString(),
                                LocalDateTime.now()
                        )
                );
    }

    @PutMapping("/by-id/{id}")
    public ResponseEntity<ApiSuccessResponse> getById(@PathVariable Long id, @RequestBody UpdateUserDto updateUserDto) {
        UpdateUserResponse updatedUser = userService.updateById(id, updateUserDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "User successfully updated!",
                                updatedUser.toString(),
                                LocalDateTime.now()
                        )
                );
    }

    @PutMapping("/by-username/{username}")
    public ResponseEntity<ApiSuccessResponse> getByUsername(@PathVariable String username, @RequestBody UpdateUserDto updateUserDto) {
        UpdateUserResponse updatedUser = userService.updateByUsername(username, updateUserDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "User successfully updated!",
                                updatedUser.toString(),
                                LocalDateTime.now()
                        )
                );
    }

    @PutMapping("/by-email/{email}")
    public ResponseEntity<ApiSuccessResponse> getByEmail(@PathVariable String email, @RequestBody UpdateUserDto updateUserDto) {
        UpdateUserResponse updatedUser = userService.updateByEmail(email, updateUserDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "User successfully updated!",
                                updatedUser.toString(),
                                LocalDateTime.now()
                        )
                );
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiSuccessResponse> updatePasswordByEmail(@RequestBody UserPasswordUpdateDto passwordUpdateDto) {
        UpdateUserResponse updatedUser = userService.updatePasswordByEmail(passwordUpdateDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "Password for: " + passwordUpdateDto.getEmail() + " successfully updated!",
                                updatedUser.getEmail(),
                                LocalDateTime.now()
                        )
                );
    }

    @DeleteMapping("/by-id/{id}")
    public ResponseEntity<ApiSuccessResponse> deleteById(@PathVariable Long id) {
        DeleteUserResponse deletedUser = userService.deleteById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "User successfully deleted!",
                                deletedUser.toString(),
                                LocalDateTime.now()
                        )
                );
    }

    @DeleteMapping("/by-username/{username}")
    public ResponseEntity<ApiSuccessResponse> deleteByUsername(@PathVariable String username) {
        DeleteUserResponse deletedUser = userService.deleteByUsername(username);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "User successfully deleted!",
                                deletedUser.toString(),
                                LocalDateTime.now()
                        )
                );
    }

    @DeleteMapping("/by-email/{email}")
    public ResponseEntity<ApiSuccessResponse> deleteByEmail(@PathVariable String email) {
        DeleteUserResponse deletedUser = userService.deleteByEmail(email);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "User successfully deleted!",
                                deletedUser.toString(),
                                LocalDateTime.now()
                        )
                );
    }

    @DeleteMapping
    public ResponseEntity<ApiSuccessResponse> deleteAll() {
        userService.deleteAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "All users successfully deleted!",
                                "",
                                LocalDateTime.now()
                        )
                );
    }
}
