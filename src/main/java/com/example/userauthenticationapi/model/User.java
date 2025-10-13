package com.example.userauthenticationapi.model;

import com.example.userauthenticationapi.model.converter.RoleConverter;
import com.example.userauthenticationapi.model.enums.Role;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class User extends BaseEntity {
    private String email;

    private String fullName;

    private String username;

    private String password;

    @Convert(converter = RoleConverter.class)
    private Role role;

    private String verificationCode;

    private LocalDateTime verificationCodeExpiresAt;

    private boolean isAccountVerified;
}
