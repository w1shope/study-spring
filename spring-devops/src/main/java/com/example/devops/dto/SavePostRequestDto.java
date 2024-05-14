package com.example.devops.dto;

import lombok.Getter;

@Getter
public class SavePostRequestDto {

    private Long userId; // 게시물 작성자
    private String title; // 게시물 제목
    private String content; // 게시물 내용
}
