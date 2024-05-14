package com.example.devops.service;

import com.example.devops.dto.GetCommentRequestDto;
import com.example.devops.dto.GetCommentResponseDto;
import com.example.devops.dto.SaveCommentRequestDto;
import com.example.devops.dto.SaveCommentResponseDto;
import com.example.devops.entity.Comment;
import com.example.devops.entity.Post;
import com.example.devops.entity.User;
import com.example.devops.repository.CommentRepository;
import com.example.devops.repository.PostRepository;
import com.example.devops.repository.UserRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public GetCommentResponseDto getComment(GetCommentRequestDto request) {
        Comment comment = findComment(request.getCommentId());

        return GetCommentResponseDto.of(comment.getId(), comment.getPost().getId(),
            comment.getUser().getId(), comment.getContent());
    }

    @Transactional
    public SaveCommentResponseDto saveComment(SaveCommentRequestDto request) {
        User user = getUser(request.getUserId());
        Post post = getPost(request.getPostId());

        Comment comment = Comment.createComment(user, post, request.getContent());
        Comment savedComment = commentRepository.save(comment);

        return SaveCommentResponseDto.of(savedComment.getId());
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new NoSuchElementException(
                "Not found comment, ID : " + commentId));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(
                () -> new NoSuchElementException("Not found post, ID : " + postId));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(
                () -> new NoSuchElementException("Not found user, ID : " + userId));
    }
}
