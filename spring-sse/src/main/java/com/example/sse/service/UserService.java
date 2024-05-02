package com.example.sse.service;

import com.example.sse.dto.EventPayload;
import com.example.sse.dto.SaveUserRequestDto;
import com.example.sse.dto.SaveUserResponseDto;
import com.example.sse.entity.User;
import com.example.sse.repository.UserRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SseEmitterService emitterService;
    private final UserRepository userRepository;

    public SaveUserResponseDto save(Long userId, SaveUserRequestDto request) {
        User user = createUser(request);
        userRepository.save(user); // 신규 사용자 저장

        User targetUser = getTargetUser(userId); // 관리자 정보

        // 관리자에게 신규 사용자 정보를 실시간으로 전송
        emitterService.broadcast(targetUser.getId(),
            EventPayload.builder()
                .userId(user.getId().toString())
                .username(user.getUsername())
                .build()
        );

        return SaveUserResponseDto.builder().userId(user.getId()).username(user.getUsername())
            .build();
    }

    private User getTargetUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. userId : " + userId));
    }

    private static User createUser(SaveUserRequestDto request) {
        return User.builder()
            .username(request.getUsername())
            .build();
    }
}
