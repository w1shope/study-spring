package com.eazybytes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// 커스텀 SecurityFilterChain
@Configuration
public class ProjectSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        /**
         * authenticated() : 인증 필요
         * permitAll() : 인증 필요 없음
         */
        http.authorizeHttpRequests((requests) -> {
                requests.requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                    .requestMatchers("/notices", "/contact").permitAll();
            }).formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());
        return http.build();

        /**
         * denyAll() : 인증이 성공하더라도 요청이 거부된다. -> 403 Error
         */
        /*http.authorizeHttpRequests((requests) -> {
            requests.anyRequest().denyAll();
            }).formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());
        return http.build();*/

        /**
         * permitAll() : 모든 요청을 허락한다.
         */
       /*http.authorizeHttpRequests((requests) -> {
                requests.anyRequest().permitAll();
            }).formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());
        return http.build();*/
    }
}
