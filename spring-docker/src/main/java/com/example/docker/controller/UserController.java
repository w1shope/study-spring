package com.example.docker.controller;

import com.example.docker.dto.SaveUserRequestDto;
import com.example.docker.entity.User;
import com.example.docker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PostMapping("/users")
    public ResponseEntity<String> saveUser(@RequestBody SaveUserRequestDto request) {
        User savedUser = userRepository.save(User.builder().username(request.getUsername()).build());
        return ResponseEntity.ok(savedUser.getUsername());
    }
}
