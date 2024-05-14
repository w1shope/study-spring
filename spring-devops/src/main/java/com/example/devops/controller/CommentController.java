package com.example.devops.controller;

import com.example.devops.dto.GetCommentRequestDto;
import com.example.devops.dto.GetCommentResponseDto;
import com.example.devops.dto.SaveCommentRequestDto;
import com.example.devops.dto.SaveCommentResponseDto;
import com.example.devops.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<GetCommentResponseDto> getComment(GetCommentRequestDto request) {
        GetCommentResponseDto response = commentService.getComment(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<SaveCommentResponseDto> saveComment(
        @RequestBody SaveCommentRequestDto request) {
        SaveCommentResponseDto response = commentService.saveComment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
