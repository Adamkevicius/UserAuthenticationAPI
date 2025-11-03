package com.example.userauthenticationapi.dto.request;

import lombok.Data;

@Data
public class ResendVerificationCodeDto {
    private String email;
}
