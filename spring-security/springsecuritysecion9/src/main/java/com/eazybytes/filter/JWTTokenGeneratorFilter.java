package com.eazybytes.filter;

import com.eazybytes.constants.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * OncePerRequestFilter : 각 요청당 반드시 한 번만 필터를 호출
 * addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)
 */

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        /**
         * 현재 Filter를 BasicAuthentication Filter가 호출 된 이후에 호출되도록 할 것이다.
         * BasicAuthentication Filter는 사용자의 자격증명 정보를 가지고 올바른 요청인지 확인하고,
            해당 사용자의 자격증명을 Security Context에 저장한다.
         * Security Context에 사용자의 자격증명 정보가 저장되어 있으므로 꺼내어 쓴다.
         */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) {
            /**
             * SecurityConstants.JWT_KEY : 서버에서 저장하고 있는 비밀키
             * 비밀키는 유출되지 않도록 주의한다. -> 런타임에 injection
             */
            SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
            /**
             * Jwts.builder를 사용하여 JWT Token 생성
             * issuer : JWT 토큰을 생성하는 개인 혹은 조직
             * subject :
             * claim : payload에 포함할 정보 key:value 형식
               claim에 비밀번호를 넣으면 안 된다. -> claim은 payload에 포함할 정보인데, payload는 디코딩할 수 있기 때문이다.
             * issuedAt : JWT Token이 발행된 날짜
             * expiration : JWT Token 만료 날짜 -> 현재시간 + 추가 시간(ms 단위)
             * sign : JWT Token에 디지털 서명을 한다.
             */
            String jwt = Jwts.builder().setIssuer("Eazy Bank").setSubject("JWT Token")
                .claim("username", authentication.getName())
                .claim("authorities", populateAuthorities(authentication.getAuthorities()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 30000000))
                .signWith(key).compact();
            /**
             * 응답에 보낼 Header 정보를 추가한다.
             * key : SecurityConstants.JWT_HEADER(Authorization), value : jwt
             * Header를 브라우저가 받을 수 있도록 Security Config에서 setExposedHeaders()에 Authorization 이름을 추가해야 한다.
               -> config.setExposedHeaders(Arrays.asList("Authorization"));
             */
            response.setHeader(SecurityConstants.JWT_HEADER, jwt);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Spring Security가 특정 상황에서만 현재 Filter를 호출하도록 하기 위해서 설정하는 메서드
     * 요청 URL이 "/user"일 때만 현재 filter를 호출한다.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/user");
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<String> authoritiesSet = new HashSet<>();
        for (GrantedAuthority authority : collection) {
            authoritiesSet.add(authority.getAuthority());
        }
        return String.join(",", authoritiesSet);
    }
}
