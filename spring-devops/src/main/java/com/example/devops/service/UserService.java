package com.example.devops.service;

import com.example.devops.dto.GetUserRequestDto;
import com.example.devops.dto.GetUserResponseDto;
import com.example.devops.dto.LoginRequestDto;
import com.example.devops.dto.SaveUserRequestDto;
import com.example.devops.dto.SaveUserResponseDto;
import com.example.devops.dto.UpdateUserProfileImageRequestDto;
import com.example.devops.entity.Role;
import com.example.devops.entity.User;
import com.example.devops.repository.UserRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 패스워드 암호화

    public GetUserResponseDto getUser(GetUserRequestDto request) {
        User user = findUserById(request.getUserId());
        return GetUserResponseDto.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .pwd(user.getPassword())
            .profileImageUrl(user.getProfileImage())
            .build();
    }

    public SaveUserResponseDto saveUser(SaveUserRequestDto request) {
        User user = createUser(request, passwordEncoder);
        User savedUser = userRepository.save(user);

        return SaveUserResponseDto.builder()
            .userId(savedUser.getId())
            .username(savedUser.getUsername())
            .email(savedUser.getEmail())
            .pwd(passwordEncoder.encode(savedUser.getPassword()))
            .build();
    }

    @Transactional
    public void updateUser(UpdateUserProfileImageRequestDto request) {
        User user = findUserById(request.getUserId());
        user.updateProfileImage(request.getProfileImage());
    }

    // 로그인 로직
    public User login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new NoSuchElementException("Not Found User : " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password is not correct");
        }

        return user;
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("Not Found User Id : " + userId));
    }

    private static User createUser(SaveUserRequestDto request, PasswordEncoder passwordEncoder) {
        return User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
    }

    /**
     * Spring Security 로그인 처리
     * @param email : 로그인 화면에서 입력한 email
     * @return UserDetails : Spring Security에서 사용자 인증을 위해 사용하는 UserDetails 객체
                             단, 여기서 User는 UserDetails를 상속받은 User 객체
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Not Found User : " + email));

        return org.springframework.security.core.userdetails.User.builder().username(user.getEmail())
            .password(user.getPassword()).roles(user.getRole().name()).build();

    }
}
