package com.example.devops.config;


import com.example.devops.entity.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // 모든 요청 URL이 Spring Security 제어를 받도록 한다.
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
            csrf(csrfConfig -> {
                csrfConfig.disable();
            }) // csrf(Cross site Request forgery) 설정 disable
            .headers(headerConfig -> {
                headerConfig.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable());
            }) //
            .authorizeHttpRequests(authorizeRequests -> {
                authorizeRequests
                    .requestMatchers("/", "/login/**", "/register/**")
                    .permitAll() // 메인 화면 및 로그인 화면은 모든 접근 허용
                    .requestMatchers("/posts/**")
                    .hasRole(Role.USER.name()) // posts 관련 요청은 USER 권한을 획득한 사용자만 접근 가능
                    .requestMatchers("/admins/**")
                    .hasRole(Role.ADMIN.name()) // admins 관련 요청은 ADMIN 권한을 획득한 사용자만 접근 가능
                    .anyRequest().authenticated();
            }) // HTTP 요청에 따른 접근 제어 설정
            .exceptionHandling(exceptionConfig -> {
                // authenticationEntryPoint : 인증 실패
                // accessDeniedHandler : 인가 실패
                exceptionConfig.authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect("/login");
                });
            }) // 인증 실패 시 Spring Security의 디폴트 값(401 에러)으로 처리되는 것이 아닌, /login 페이지로 리다이렉트하도록 처리
            .formLogin(formLogin -> {
                formLogin
                    .loginPage("/login") // GET, 로그인 화면 URL
                    .usernameParameter("email") // 로그인에 필요한 ID를 username으로 설정, 디폴트 username
                    .passwordParameter("password") // 로그인에 필요한 PWD를 password로 설정, 디폴트 password
                    .loginProcessingUrl("/login") // POST, 로그인 요청을 보낼 URL
                    .defaultSuccessUrl("/"); // 로그인을 성공하였을 때 이동할 URL
            }) // 로그인 설정
            .logout(logoutConfig -> {
                logoutConfig.logoutSuccessUrl("/"); // 로그아웃에 성공하였을 때 이동할 URL
            }); // 로그아웃 설정

        return http.build(); // HttpSecurity -> SecurityFilterChain 변환
    }

    /**
     * 비밀번호를 BCrypt 알고리즘으로 암호화
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder() {
            /**
             * 사용자가 입력한 비밀번호를 암호화
             * @param rawPassword : 사용자가 입력한 원본 비밀번호
             * @return : 암호화된 비밀번호
             */
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            /**
             * 사용자가 입력한 비밀번호와 DB에 저장된 암호화된 비밀번호가 일치하는지 확인
             * loadUserByUsername()에 의해 생성된 UserDetails.getPassword()가 encodedPassword로 전달됨
             * @param rawPassword : 사용자가 입력한 원본 비밀번호
             * @param encodedPassword : DB에 저장된 암호화된 비밀번호
             * @return : 일치하면 true, 불일치하면 false
             */
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return rawPassword.toString().equals(encodedPassword);
            }
        };
    }
}
