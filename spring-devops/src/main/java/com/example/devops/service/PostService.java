package com.example.devops.service;

import com.example.devops.dto.GetNotificationResponseDto;
import com.example.devops.dto.GetPostRequestDto;
import com.example.devops.dto.GetPostResponseDto;
import com.example.devops.dto.SavePostRequestDto;
import com.example.devops.dto.SavePostResponseDto;
import com.example.devops.entity.Post;
import com.example.devops.entity.User;
import com.example.devops.repository.CommentRepository;
import com.example.devops.repository.PostRepository;
import com.example.devops.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public GetPostResponseDto getPost(GetPostRequestDto request) {
        Post post = findPost(request.getPostId());
        return GetPostResponseDto.createPost(post.getId(), post.getTitle(), post.getContent(),
            post.getUser().getId());
    }

    @Transactional
    public SavePostResponseDto savePost(SavePostRequestDto request) {
        User user = getUser(request.getUserId());
        Post post = getCreatePost(request, user);
        Post savedPost = postRepository.save(post);

        return convertResponse(savedPost);
    }

    @Transactional(readOnly = true)
    public LocalDateTime getLastNotifiedTime(Long postId) {
        String lastSavedTime = redisTemplate.opsForValue().get(postId.toString());

        return lastSavedTime != null ? convertStringToLocalDateTime(lastSavedTime) : null;
    }

    @Transactional
    public List<GetNotificationResponseDto> getNewComments(Long postId, LocalDateTime lastNotifiedTime) {
        updateLastCommentTimeInRedis(postId);
        log.info("lastNotifiedTime : {}", lastNotifiedTime);

        return convertResponse(postId, lastNotifiedTime);
    }

    private static SavePostResponseDto convertResponse(Post savedPost) {
        return SavePostResponseDto.createPost(savedPost.getId(), savedPost.getTitle(),
            savedPost.getContent(), savedPost.getUser().getId());
    }

    private static Post getCreatePost(SavePostRequestDto request, User user) {
        return Post.builder()
            .user(user)
            .title(request.getTitle())
            .content(request.getContent())
            .build();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new NoSuchElementException("Not found user, ID : " + userId));
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
            () -> new NoSuchElementException("Not found post, ID : " + postId));
    }

    // String -> LocalDateTime 변환
    private static LocalDateTime convertStringToLocalDateTime(String lastCommentTime) {
        return LocalDateTime.parse(lastCommentTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private void updateLastCommentTimeInRedis(Long postId) {
        redisTemplate.opsForValue().set(postId.toString(), getNewCommentTime(postId).toString());
    }

    private LocalDateTime getNewCommentTime(Long postId) {
        return commentRepository.findTopByPostIdOrderByCreatedDateDesc(postId).getCreatedDate();
    }

    private List<GetNotificationResponseDto> convertResponse(Long postId,
        LocalDateTime lastNotifiedTime) {
        if (lastNotifiedTime == null) {
            return commentRepository.findAllByPostId(postId).stream()
                .map(GetNotificationResponseDto::of).toList();
        }

        return commentRepository.getNewNotificationComments(
                lastNotifiedTime, postId).stream()
            .map(GetNotificationResponseDto::of)
            .toList();
    }
}

