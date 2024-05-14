package com.example.devops.controller;

import com.example.devops.dto.GetNotificationResponseDto;
import com.example.devops.dto.GetPostRequestDto;
import com.example.devops.dto.GetPostResponseDto;
import com.example.devops.dto.SavePostRequestDto;
import com.example.devops.dto.SavePostResponseDto;
import com.example.devops.service.PostService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<GetPostResponseDto> getPost(GetPostRequestDto request) {
        GetPostResponseDto response = postService.getPost(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<SavePostResponseDto> savePost(@RequestBody SavePostRequestDto request) {
        SavePostResponseDto response = postService.savePost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/notification/{postId}")
    public ResponseEntity<List<GetNotificationResponseDto>> getNotification(@PathVariable Long postId) {
        LocalDateTime lastNotifiedTime = postService.getLastNotifiedTime(postId);
        List<GetNotificationResponseDto> response = postService.getNewComments(postId,
            lastNotifiedTime);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
