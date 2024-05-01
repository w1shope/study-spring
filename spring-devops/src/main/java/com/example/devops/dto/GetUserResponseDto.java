package com.example.devops.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetUserResponseDto {

    private Long userId;
    private String email;
    private String pwd;
    private String username;
    private String profileImageUrl;
}
