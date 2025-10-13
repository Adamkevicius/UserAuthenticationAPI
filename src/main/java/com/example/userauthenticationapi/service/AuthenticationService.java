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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepo userRepo;

    private final AuthenticationManager authManager;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    public User signUp(RegisterUserDto registerUserDto) {
        User user = new User();

        user.setEmail(registerUserDto.getEmail());
        user.setUsername(registerUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setRole(Role.User);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));
        user.setAccountVerified(false);
        emailService.createAndSendVerificationEmail(user);

        return userRepo.save(user);
    }

    public User authenticate(LoginUserDto loginUserDto) {
        User user = userRepo.findByEmail(loginUserDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setAccountVerified(false);

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.getEmail(), loginUserDto.getPassword()
                )
        );

        emailService.createAndSendVerificationEmail(user);

        return user;
    }

    public void verifyUser(VerifyUserDto verifyUserDto) {
        Optional<User> optionalUser = userRepo.findByEmail(verifyUserDto.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired.");
            }

            if (user.getVerificationCode().equals(verifyUserDto.getVerificationCode())) {
                user.setAccountVerified(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);

                userRepo.save(user);
            }
            else {
                throw new RuntimeException("Invalid verification code.");
            }
        }
        else {
            throw new RuntimeException("User not found.");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepo.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));

            emailService.createAndSendVerificationEmail(user);

            userRepo.save(user);
        }
    }

    private String generateVerificationCode() {
        final SecureRandom secureRandom = new SecureRandom();
        int verificationCode = secureRandom.nextInt(900000) + 100000;

        return String.valueOf(verificationCode);
    }
}
