package com.example.devops.service;

import com.example.devops.dto.GetPostRequestDto;
import com.example.devops.dto.GetPostResponseDto;
import com.example.devops.dto.SavePostRequestDto;
import com.example.devops.dto.SavePostResponseDto;
import com.example.devops.entity.Post;
import com.example.devops.entity.User;
import com.example.devops.repository.PostRepository;
import com.example.devops.repository.UserRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

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
}

