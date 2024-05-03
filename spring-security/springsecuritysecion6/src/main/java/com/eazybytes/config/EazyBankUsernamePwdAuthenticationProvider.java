package com.eazybytes.config;

import com.eazybytes.model.Customer;
import com.eazybytes.repository.CustomerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EazyBankUsernamePwdAuthenticationProvider implements AuthenticationProvider {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        String username = authentication.getName(); // 사용자가 입력한 username
        String password = authentication.getCredentials().toString(); // 사용자가 입력한 password

        List<Customer> customers = customerRepository.findByEmail(username);
        // 조회된 사용자가 있다면 DB에 저장된 정보와 일치하는지 확인한다.
        if(!customers.isEmpty()) {
            if (passwordEncoder.matches(password, customers.get(0).getPwd())) {
                List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority(customers.get(0).getRole()));
                return new UsernamePasswordAuthenticationToken(username, password, authorities);
            } else {
                throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            throw new BadCredentialsException("사용자가 조회되지 않습니다.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
