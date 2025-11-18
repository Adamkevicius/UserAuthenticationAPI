package com.example.userauthenticationapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteUserResponse {
    private Long id;

    private String email;

    private String username;

    private LocalDateTime deletedAt;
}
