package com.example.sse.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveUserResponseDto {

    private Long userId;
    private String username;
}
