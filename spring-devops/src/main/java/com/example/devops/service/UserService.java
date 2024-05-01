package com.example.devops.service;

import com.example.devops.dto.GetPresignedUrlRequestDto;
import com.example.devops.dto.GetUserRequestDto;
import com.example.devops.dto.GetUserResponseDto;
import com.example.devops.dto.PresignedUrlStatus;
import com.example.devops.dto.SaveUserRequestDto;
import com.example.devops.dto.SaveUserResponseDto;
import com.example.devops.dto.UpdateUserProfileImageRequestDto;
import com.example.devops.entity.User;
import com.example.devops.repository.UserRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public GetUserResponseDto getUser(GetUserRequestDto request) {
        User user = findUserById(request.getUserId());
        return GetUserResponseDto.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .pwd(user.getPwd())
            .profileImageUrl(user.getProfileImage())
            .build();
    }

    public SaveUserResponseDto saveUser(SaveUserRequestDto request) {
        User user = createUser(request);
        User savedUser = userRepository.save(user);

        return SaveUserResponseDto.builder()
            .userId(savedUser.getId())
            .username(savedUser.getUsername())
            .email(savedUser.getEmail())
            .pwd(savedUser.getPwd())
            .build();
    }

    @Transactional
    public void updateUser(UpdateUserProfileImageRequestDto request) {
        User user = findUserById(request.getUserId());
        user.updateProfileImage(request.getProfileImage());
    }

    private static GetPresignedUrlRequestDto createPresignedRequest(String profileImageUrl) {
        return GetPresignedUrlRequestDto.builder()
            .httpMethod(PresignedUrlStatus.PUT)
            .fileName(profileImageUrl)
            .build();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("Not Found User Id : " + userId));
    }

    private static User createUser(SaveUserRequestDto request) {
        return User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .pwd(request.getPwd())
            .build();
    }
}
