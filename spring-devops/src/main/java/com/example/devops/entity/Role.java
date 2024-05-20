package com.example.devops.entity;

import lombok.Getter;

@Getter
public enum Role {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String type;

    Role(String type) {
        this.type = type;
    }
}
