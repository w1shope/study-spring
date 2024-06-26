package com.example.devops.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SaveUserRequestDto {

    private String username;
    private String email;
    private String password;
}
