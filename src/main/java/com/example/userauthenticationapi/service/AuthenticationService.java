package com.example.userauthenticationapi.service;

import com.example.userauthenticationapi.exception.ConflictException;
import com.example.userauthenticationapi.exception.ResourceNotFoundException;
import com.example.userauthenticationapi.exception.UnauthorizedException;
import com.example.userauthenticationapi.exception.ValidationException;
import com.example.userauthenticationapi.model.User;
import com.example.userauthenticationapi.model.enums.Role;
import com.example.userauthenticationapi.repo.UserRepo;
import com.example.userauthenticationapi.dto.request.LoginUserDto;
import com.example.userauthenticationapi.dto.request.RegisterUserDto;
import com.example.userauthenticationapi.dto.request.ResendVerificationCodeDto;
import com.example.userauthenticationapi.dto.request.VerifyUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

        String email = registerUserDto.getEmail();
        String username = registerUserDto.getUsername();

        if (userRepo.existsByEmail(email)) {
            throw new ConflictException("Email is already registered.");
        }

        if (userRepo.existsByUsername(username)) {
            throw new ConflictException("Username already taken.");
        }

        user.setEmail(registerUserDto.getEmail());
        user.setUsername(registerUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setRole(Role.User);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));
        user.setAccountVerified(false);

        emailService.sendEmail(user);

        userRepo.save(user);
    }

    public void authenticate(LoginUserDto loginUserDto) {
        String email = loginUserDto.getEmail();
        String password = loginUserDto.getPassword();
        User user = userRepo.findByEmail(loginUserDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found."));

        if (!passwordEncoder.matches(password, user.getPassword()))
        {
            throw new ValidationException("Password is incorrect.");
        }

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(), password
                )
        );

        if (auth.isAuthenticated()) {
            user.setAccountVerified(false);

            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));
            userRepo.save(user);

            emailService.sendEmail(user);
        }
        else {
            throw new UnauthorizedException("User is not authenticated. Please log in first.");
        }
    }

    public String verifyUser(VerifyUserDto verifyUserDto) {
        User user = userRepo.findByEmail(verifyUserDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        String jwtToken;

        if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new UnauthorizedException("Verification code has expired.");
        }

        if (user.getVerificationCode().equals(verifyUserDto.getVerificationCode())) {
            user.setAccountVerified(true);
            user.setVerificationCode(null);
            user.setVerificationCodeExpiresAt(null);

            userRepo.save(user);

            jwtToken = jwtService.generateToken(user.getUsername());
        }
        else {
            throw new UnauthorizedException("Verification code is invalid.");
        }

        return jwtToken;
    }

    public void resendVerificationCode(ResendVerificationCodeDto resendVerificationCodeDto) {
        String email = resendVerificationCodeDto.getEmail();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        String newVerificationCode = generateVerificationCode();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        user.setVerificationCode(newVerificationCode);
        user.setVerificationCodeExpiresAt(expirationTime);

        userRepo.save(user);

        emailService.sendEmail(user);
    }

    private String generateVerificationCode() {
        final SecureRandom secureRandom = new SecureRandom();
        int verificationCode = secureRandom.nextInt(900000) + 100000;

        return String.valueOf(verificationCode);
    }
}
