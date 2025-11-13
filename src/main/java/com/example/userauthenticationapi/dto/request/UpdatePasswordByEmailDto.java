package com.example.userauthenticationapi.dto.request;

import lombok.Data;

@Data
public class UpdatePasswordByEmailDto {
    private final String email;

    private final String password;
}
