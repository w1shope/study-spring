package com.example.devops.dto;

import lombok.Getter;

@Getter
public class SaveUserRequestDto {

    private String username;
    private String email;
    private String pwd;
}
