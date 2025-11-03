package com.example.userauthenticationapi.dto.request;

import lombok.Data;

@Data
public class RegisterUserDto {
    private String email;

    private String username;

    private String password;
}
