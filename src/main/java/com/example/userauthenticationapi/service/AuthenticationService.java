package com.example.userauthenticationapi.service;

import com.example.userauthenticationapi.model.User;
import com.example.userauthenticationapi.model.enums.Role;
import com.example.userauthenticationapi.repo.UserRepo;
import com.example.userauthenticationapi.security.dto.LoginUserDto;
import com.example.userauthenticationapi.security.dto.RegisterUserDto;
import com.example.userauthenticationapi.security.dto.ResendVerificationCodeDto;
import com.example.userauthenticationapi.security.dto.VerifyUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepo userRepo;

    private final AuthenticationManager authManager;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final JwtService jwtService;

    public void signUp(RegisterUserDto registerUserDto) {
        User user = new User();

        user.setEmail(registerUserDto.getEmail());
        user.setUsername(registerUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setRole(Role.User);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));
        user.setAccountVerified(false);

        emailService.createAndSendVerificationEmail(user);
    }

    public void authenticate(LoginUserDto loginUserDto) {
        User user = userRepo.findByEmail(loginUserDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(), loginUserDto.getPassword()
                )
        );

        if (auth.isAuthenticated()) {
            user.setAccountVerified(false);
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));
            userRepo.save(user);

            emailService.createAndSendVerificationEmail(user);
        }
    }

    public String verifyUser(VerifyUserDto verifyUserDto) {
        User user = userRepo.findByEmail(verifyUserDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found with email: " + verifyUserDto.getEmail())
                );
        String jwtToken;

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired.");
        }

        if (user.getVerificationCode().equals(verifyUserDto.getVerificationCode())) {
            user.setAccountVerified(true);
            user.setVerificationCode(null);
            user.setVerificationCodeExpiresAt(null);

            userRepo.save(user);

            jwtToken = jwtService.generateToken(user.getUsername());
        }
        else {
            throw new RuntimeException("Invalid verification code.");
        }

        return jwtToken;
    }

    public void resendVerificationCode(ResendVerificationCodeDto resendVerificationCodeDto) {
        String email = resendVerificationCodeDto.getEmail();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));

        userRepo.save(user);

        emailService.createAndSendVerificationEmail(user);
    }

    private String generateVerificationCode() {
        final SecureRandom secureRandom = new SecureRandom();
        int verificationCode = secureRandom.nextInt(900000) + 100000;

        return String.valueOf(verificationCode);
    }
}
