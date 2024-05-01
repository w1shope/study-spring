package com.example.devops.dto;

import lombok.Getter;

@Getter
public class UpdateUserProfileImageRequestDto {

    private Long userId;
    private String profileImage;
}
