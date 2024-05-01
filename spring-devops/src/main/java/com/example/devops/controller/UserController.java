package com.example.devops.controller;

import com.example.devops.dto.GetUserRequestDto;
import com.example.devops.dto.GetUserResponseDto;
import com.example.devops.dto.SaveUserRequestDto;
import com.example.devops.dto.SaveUserResponseDto;
import com.example.devops.dto.UpdateUserProfileImageRequestDto;
import com.example.devops.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 사용자 조회
     */
    @GetMapping
    public ResponseEntity<GetUserResponseDto> findUser(@ModelAttribute GetUserRequestDto request) {
        GetUserResponseDto response = userService.getUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * 사용자 등록
     */
    @PostMapping
    public ResponseEntity<SaveUserResponseDto> saveUser(@RequestBody SaveUserRequestDto request) {
        SaveUserResponseDto response = userService.saveUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 사용자 프로필 이미지 수정
     */
    @PatchMapping
    public ResponseEntity<Void> updateUserProfileImage(@RequestBody UpdateUserProfileImageRequestDto request) {
        userService.updateUser(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
