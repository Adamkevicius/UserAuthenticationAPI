package com.example.userauthenticationapi.repo;

import com.example.userauthenticationapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationCode(String verificationCode);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    User getByUsername(String username);

    User getByEmail(String email);

    void deleteByUsername(String username);

    void deleteByEmail(String email);
}
