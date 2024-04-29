package com.eazybytes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// 커스텀 SecurityFilterChain
@Configuration
public class ProjectSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
            .authorizeHttpRequests((requests) -> {
                requests.requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards")
                    .authenticated()
                    .requestMatchers("/notices", "/contact", "/register").permitAll();
            }).formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    /**
     * NoOpPasswordEncoder() : 비밀번호를 일반 텍스트로 저장한다. 비밀번호 암호화 및 해싱을 수행하지 않기 때문에 prod 환경에서 사용 금지.
     * BCrypt() : 해싱 알고리즘
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
//        return NoOpPasswordEncoder.getInstance();
    }
}
