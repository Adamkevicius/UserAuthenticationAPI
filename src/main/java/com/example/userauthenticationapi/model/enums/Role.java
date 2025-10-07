package com.example.userauthenticationapi.model.enums;

import lombok.Getter;

@Getter
public enum Role {
    User, Admin;

    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.toString().equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + role);
    }
}
