package com.example.userauthenticationapi.dto.request;

import lombok.Data;

@Data
public class VerifyUserDto {
    private String email;

    private String verificationCode;
}
