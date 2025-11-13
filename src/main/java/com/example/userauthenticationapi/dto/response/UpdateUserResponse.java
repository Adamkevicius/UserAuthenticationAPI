package com.example.userauthenticationapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UpdateUserResponse {
    private Long id;

    private String email;

    private String username;

    private LocalDateTime updatedAt;
}
