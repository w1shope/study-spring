package com.example.devops.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetPresignedUrlRequestDto {

    private Long userId;
    private PresignedUrlStatus httpMethod; // GET, PUT
    private String fileName;
}
