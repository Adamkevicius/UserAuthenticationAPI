package com.example.userauthenticationapi.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class ApiResponse {
    private boolean success;

    private String message;

    private LocalDateTime timestamp;
}
