package com.eazybytes.filter;

import com.eazybytes.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 클라이언트로부터 받은 JWT Token이 유효한지 확인하는 Filter
 */
public class JWTTokenValidatorFilter  extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        /**
         * 요청 헤더에 담긴 SecurityConstants.JWT_HEADER(Authorization)의 value 값을 가져온다.
         */
        String jwt = request.getHeader(SecurityConstants.JWT_HEADER);
        if (null != jwt) {
            try {
                /**
                 * hmacShaKeyFor()를 사용하여 비밀키 생성
                 * 클라이언트의 서명과 서버에서 생성한 서명이 일치하는지 확인하기 위해 생성
                 */
                SecretKey key = Keys.hmacShaKeyFor(
                    SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

                Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key) // 비밀키 저장
                    .build()
                    .parseClaimsJws(jwt) // payload에 담긴 claim 파싱
                    .getBody(); // 토큰에 담긴 payload 반환

                String username = String.valueOf(claims.get("username"));
                String authorities = (String) claims.get("authorities");

                /**
                 * 위 정보를 이용하여 UsernamePasswordAuthenticationToken() 생성
                 */
                Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));

                /**
                 * 인증 객체를 SecurityContextHolder에 저장
                 * 인증 과정이 성공적으로 처리되었다는 것을 의미
                 */
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid Token received!");
            }

        }
        filterChain.doFilter(request, response);
    }

    /**
     * /user 요청 URL은 현재 filter를 호출하지 않도록 설정
       왜냐하면 클라이언트가 보낸 JWT Token를 서버에서 파싱하여 유효한 요청인지 확인하는 필터이기 때문이다.
       즉, 이전에 사용자가 이미 자격 증명 정보를 전달하여 토큰이 생성된 상태이다.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().equals("/user");
    }

}