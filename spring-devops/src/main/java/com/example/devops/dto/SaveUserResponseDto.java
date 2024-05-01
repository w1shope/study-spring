package com.example.devops.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SaveUserResponseDto {

    private Long userId;
    private String username;
    private String email;
    private String pwd;
}
