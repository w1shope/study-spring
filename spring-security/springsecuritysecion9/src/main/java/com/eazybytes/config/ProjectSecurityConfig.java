package com.eazybytes.config;

import com.eazybytes.filter.AuthoritiesLoggingAfterFilter;
import com.eazybytes.filter.AuthoritiesLoggingAtFilter;
import com.eazybytes.filter.JWTTokenGeneratorFilter;
import com.eazybytes.filter.JWTTokenValidatorFilter;
import com.eazybytes.filter.RequestValidationBeforeFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
public class ProjectSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            /**
             * HTTP Statelss 상태로 사용, JSESSIONID 생성 X
             */
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(
                corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(
                            Collections.singletonList("http://localhost:4200"));
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);
                        /**
                         *
                         */
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        config.setMaxAge(3600L);
                        return config;
                    }
                }))
            /**
             * BasicAuthenticationFilter : 사용자가 입력한 정보를 토대로 인증을 담당하는 필터
             * addFilterBefore() : 두 번째 파라미터로 넘기는 Filter를 호출하기 전에 첫 번째 파라미터로 넘긴 커스텀 필터를 먼저 호출한다.
             * addFilterAfter() : 두 번째 파라미터에 담긴 Filter를 먼저 실행하고, 첫 번째 파라미터에 담긴 Filter 호출한다.
             * addFilterAt(): 첫 번째 파라미터에 담긴 Filter를 두 번째 파라미터에 담긴 Filter와 동일한 시점에 호출한다.
                              두 필터 간의 호출 순서를 보장하지 않기 때문에 매우 유의해야 한다.
             */
            .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class)
            .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
            // JWT 토큰을 생성하는 filter가 사용자의 자격증명이 Security Context에 저장된 후 호출되도록 설정
            .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
            .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)
            .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)
            .authorizeHttpRequests((requests) -> requests
                .requestMatchers("/myAccount").hasRole("USER")
                .requestMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/myLoans").hasRole("USER")
                .requestMatchers("/myCards").hasRole("USER")
                .requestMatchers("/user").authenticated()
                .requestMatchers("/notices", "/contact", "/register").permitAll())
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}