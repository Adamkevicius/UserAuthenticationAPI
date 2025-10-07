package com.example.userauthenticationapi.repo;

import com.example.userauthenticationapi.model.User;

import java.util.Optional;

public interface UserRepo {
    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationCode(String verificationCode);
}
