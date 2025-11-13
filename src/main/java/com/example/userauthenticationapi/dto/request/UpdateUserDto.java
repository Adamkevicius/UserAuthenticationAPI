package com.example.userauthenticationapi.dto.request;

import lombok.Data;

@Data
public class UpdateUserDto {
    private String fullName;

    private String username;

    private String password;

}
