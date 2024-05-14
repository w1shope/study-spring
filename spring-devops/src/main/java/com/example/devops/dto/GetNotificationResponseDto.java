package com.example.devops.dto;

import com.example.devops.entity.Comment;
import lombok.Getter;

@Getter
public class GetNotificationResponseDto {

    private Long postId; // 댓글이 작성된 게시물
    private Long userId; // 댓글 작성자 id
    private String username; // 댓글 작성자 name

    private GetNotificationResponseDto(Long postId, Long userId, String username) {
        this.postId = postId;
        this.userId = userId;
        this.username = username;
    }

    public static GetNotificationResponseDto of(Comment c) {
        return new GetNotificationResponseDto(c.getPost().getId(), c.getUser().getId(), c.getUser().getUsername());
    }
}
