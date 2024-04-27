package com.eazybytes.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
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

    //    @Bean
    /*
    public InMemoryUserDetailsManager userDetailsManager() {
        //  withDefaultPasswordEncoder() : 기본으로 등록된 패스워드 인코딩 방시 사용
    UserDetails admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("12345")
            .authorities("admin")
            .build();
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("12345")
            .authorities("read")
            .build();
        return new InMemoryUserDetailsManager(admin, user);

        UserDetails admin = User.withUsername("admin")
            .password("12345")
            .authorities("admin")
            .build();
        UserDetails user = User.withUsername("user")
            .password("12345")
            .authorities("read")
            .build();
        return new InMemoryUserDetailsManager(admin, user);
    }
    */


    /**
     * JdbcUserDetailsManager : SpringSecurity에서 요구하는 DB 스키마 정보를 따라야 한다. DB에 저장된 자격증명을 사용한다.
     * DataSource는 application.yml에 작성한 DB 정보로 생성한다.
     */
//    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }

    /**
     * NoOpPasswordEncoder() : 비밀번호를 일반 텍스트로 저장한다. 비밀번호 암호화 및 해싱을 수행하지 않기 때문에 prod 환경에서 사용 금지.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
