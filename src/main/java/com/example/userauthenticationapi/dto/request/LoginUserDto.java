package com.example.userauthenticationapi.dto.request;

import lombok.Data;

@Data
public class LoginUserDto {
    private String email;

    private String password;
}
