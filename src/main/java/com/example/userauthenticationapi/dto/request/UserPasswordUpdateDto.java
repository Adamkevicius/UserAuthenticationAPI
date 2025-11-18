package com.example.userauthenticationapi.dto.request;

import lombok.Data;

@Data
public class UserPasswordUpdateDto {
    private String email;

    private String password;
}
