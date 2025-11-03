package com.example.userauthenticationapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private boolean success;

    private String message;

    private LocalDateTime timestamp;
}
