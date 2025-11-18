package com.example.userauthenticationapi.controller;

import com.example.userauthenticationapi.dto.request.LoginUserDto;
import com.example.userauthenticationapi.dto.request.RegisterUserDto;
import com.example.userauthenticationapi.dto.request.ResendVerificationCodeDto;
import com.example.userauthenticationapi.dto.request.VerifyUserDto;
import com.example.userauthenticationapi.dto.response.ApiSuccessResponse;
import com.example.userauthenticationapi.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiSuccessResponse> singUp(@RequestBody RegisterUserDto registerUserDto) {
        authService.signUp(registerUserDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "User successfully signed up. " +
                                        "Please verify your email with OTP that has been sent to your email.",
                                registerUserDto.getUsername(),
                                LocalDateTime.now()
                        )
                );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiSuccessResponse> login(@RequestBody LoginUserDto loginUserDto) {
        authService.authenticate(loginUserDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "User successfully logged in. " +
                                        "Please verify your email with OTP that has been sent to your email.",
                                loginUserDto.getEmail(),
                                LocalDateTime.now()
                        )
                );
    }

    @PostMapping("/verification-code/verify")
    public ResponseEntity<ApiSuccessResponse> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        String jwtToken = authService.verifyUser(verifyUserDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "User verified successfully.",
                                jwtToken,
                                LocalDateTime.now()
                        )
                );
    }

    @PostMapping("/verification-code/resend")
    public ResponseEntity<ApiSuccessResponse> resendVerificationCode(@RequestBody ResendVerificationCodeDto email) {
        authService.resendVerificationCode(email);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "Verification code resend successfully",
                                email.getEmail(),
                                LocalDateTime.now()
                        )
                );
    }

    @GetMapping("/check-session")
    public ResponseEntity<ApiSuccessResponse> checkSessionToken(Authentication authentication) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new ApiSuccessResponse(
                                true,
                                "JWT token is valid",
                                authentication.getName(),
                                LocalDateTime.now()
                        )
                );
    }
}
