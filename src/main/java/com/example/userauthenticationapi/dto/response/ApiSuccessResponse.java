package com.example.userauthenticationapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiSuccessResponse {
    private boolean success;

    private String message;

    private String data;

    private LocalDateTime timestamp;
}
