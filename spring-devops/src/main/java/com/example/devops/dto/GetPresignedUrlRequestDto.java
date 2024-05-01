package com.example.devops.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetPresignedUrlRequestDto {

    private PresignedUrlStatus httpMethod; // GET, PUT
    private String fileName;
}
