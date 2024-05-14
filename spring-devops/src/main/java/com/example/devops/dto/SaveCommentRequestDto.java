package com.example.devops.dto;

import lombok.Getter;

@Getter
public class SaveCommentRequestDto {

    private Long userId;
    private Long postId;
    private String content;
}
