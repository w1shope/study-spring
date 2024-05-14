package com.example.devops.dto;

import lombok.Getter;

@Getter
public class SaveCommentResponseDto {

    private Long commentId;

    private SaveCommentResponseDto(Long commentId) {
        this.commentId = commentId;
    }

    public static SaveCommentResponseDto of(Long commentId) {
        return new SaveCommentResponseDto(commentId);
    }
}
