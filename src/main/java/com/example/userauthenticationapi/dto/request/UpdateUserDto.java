package com.example.userauthenticationapi.dto.request;

import lombok.Data;

@Data
public class UpdateUserDto {
    private String email;

    private String username;

    private String password;

}
