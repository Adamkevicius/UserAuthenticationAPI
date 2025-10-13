package com.example.userauthenticationapi.service;

import com.example.userauthenticationapi.model.User;
import com.example.userauthenticationapi.model.enums.Role;
import com.example.userauthenticationapi.repo.UserRepo;
import com.example.userauthenticationapi.security.dto.LoginUserDto;
import com.example.userauthenticationapi.security.dto.RegisterUserDto;
import com.example.userauthenticationapi.security.dto.VerifyUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepo userRepo;

    private final AuthenticationManager authManager;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    // TODO ADD EMAIL SERVICE

    public User signUp(RegisterUserDto registerUserDto) {
        User user = new User();

        user.setEmail(registerUserDto.getEmail());
        user.setUsername(registerUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setRole(Role.User);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));
        user.setAccountVerified(false);
        sendVerificationCode(user);

        return null;
    }

    public User authenticate(LoginUserDto loginUserDto) {
        User user = userRepo.findByEmail(loginUserDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isAccountVerified()) {
            throw new RuntimeException("Account not verified.");
        }

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.getEmail(), loginUserDto.getPassword()
                )
        );

        return user;
    }

    public void verifyUser(VerifyUserDto verifyUserDto) {
        // TODO ADD VERIFICATION CODE CHECK AND JWT GENERATION
    }

    public void sendVerificationCode(User user) {
        // TODO ADD VERIFICATION CODE SENDING
    }

    public void resendVerificationCode(String email) {
        // TODO ADD VERIFICATION CODE RESENDING
    }

    private String generateVerificationCode() {
        final SecureRandom secureRandom = new SecureRandom();
        int verificationCode = secureRandom.nextInt(900000) + 100000;

        return String.valueOf(verificationCode);
    }
}
