package com.eazybytes.config;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

// 커스텀 SecurityFilterChain
@Configuration
public class ProjectSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.cors(
                customizer -> customizer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setMaxAge(3600L);
                    return config;
                }))
            /**
             * Spring Security는 GET 메서드에 대해서 CSRF 보호를 하지 않는다.
             * 따라서 "/notices" 에 대해서는 CSRF 보호를 할 필요가 없다.
             */
            .csrf().ignoringRequestMatchers("/contact", "/register").and()
            .authorizeHttpRequests((requests) -> {
                requests.requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards",
                        "/user").authenticated()
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
    }
}
