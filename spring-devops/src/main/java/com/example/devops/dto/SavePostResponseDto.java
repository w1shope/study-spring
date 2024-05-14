package com.example.devops.dto;

import lombok.Getter;

@Getter
public class SavePostResponseDto {

    private Long postId;
    private String title;
    private String content;
    private Long userId;

    private SavePostResponseDto(Long postId, String title, String content, Long userId) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.userId = userId;
    }

    public static SavePostResponseDto createPost(Long postId, String title, String content, Long userId) {
        return new SavePostResponseDto(postId, title, content, userId);
    }
}
