package com.example.sse.controller;

import com.example.sse.dto.SaveUserRequestDto;
import com.example.sse.dto.SaveUserResponseDto;
import com.example.sse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<SaveUserResponseDto> saveUser(@RequestBody SaveUserRequestDto request) {
        SaveUserResponseDto response = userService.save(1L, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
