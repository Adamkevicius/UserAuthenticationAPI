package com.example.userauthenticationapi.controller;

import com.example.userauthenticationapi.security.dto.LoginUserDto;
import com.example.userauthenticationapi.security.dto.RegisterUserDto;
import com.example.userauthenticationapi.security.dto.ResendVerificationCodeDto;
import com.example.userauthenticationapi.security.dto.VerifyUserDto;
import com.example.userauthenticationapi.security.response.ApiResponse;
import com.example.userauthenticationapi.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> singUp(@RequestBody RegisterUserDto registerUserDto) {
        authService.signUp(registerUserDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse(
                                true,
                                "User successfully signed up",
                                LocalDateTime.now()
                        )
                );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginUserDto loginUserDto) {
        authService.authenticate(loginUserDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse(
                                true,
                                "User successfully logged in",
                                LocalDateTime.now()
                        )
                );
    }

    @PostMapping("/verification-code/verify")
    public ResponseEntity<ApiResponse> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        String jwtToken = authService.verifyUser(verifyUserDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse(
                                true,
                                jwtToken,
                                LocalDateTime.now()
                        )
                );
    }

    @PostMapping("/verification-code/resend")
    public ResponseEntity<ApiResponse> resendVerificationCode(@RequestBody ResendVerificationCodeDto email) {
        authService.resendVerificationCode(email);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse(
                                true,
                                "Verification code resend successfully",
                                LocalDateTime.now()
                        )
                );
    }
}
