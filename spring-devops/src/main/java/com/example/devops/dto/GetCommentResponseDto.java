package com.example.devops.dto;

import lombok.Getter;

@Getter
public class GetCommentResponseDto {

    private Long commentId;
    private Long postId;
    private Long userId;
    private String content;

    private GetCommentResponseDto(Long commentId, Long postId, Long userId, String content) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }

    public static GetCommentResponseDto of(Long commentId, Long postId, Long userId, String content) {
        return new GetCommentResponseDto(commentId, postId, userId, content);
    }
}
