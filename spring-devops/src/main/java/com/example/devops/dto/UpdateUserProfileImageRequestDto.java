package com.example.devops.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateUserProfileImageRequestDto {

    private Long userId;
    private String profileImage;
}
