package com.example.devops.controller;

import com.example.devops.dto.LoginRequestDto;
import com.example.devops.dto.SaveUserRequestDto;
import com.example.devops.entity.User;
import com.example.devops.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "/login";
    }

    // 로그인 로직
    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequestDto request) {
        User user = userService.login(request);
        log.info("login user : {}", user);
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute SaveUserRequestDto request) {
        userService.saveUser(request);
        return "redirect:/";
    }
}
