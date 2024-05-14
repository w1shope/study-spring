package com.example.devops.dto;

import lombok.Getter;

@Getter
public class GetPostResponseDto {

    private Long postId;
    private String title;
    private String content;
    private Long userId;

    private GetPostResponseDto(Long postId, String title, String content, Long userId) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.userId = userId;
    }

    public static GetPostResponseDto createPost(Long postId, String title, String content, Long userId) {
        return new GetPostResponseDto(postId, title, content, userId);
    }
}
