package com.example.board.security.provider;

import com.example.board.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;

// Authentication 객체를 받아 AuthenticationProvider가 인증을 위임 처리한다.
@RequiredArgsConstructor
public class LoginAuthProvider implements AuthenticationProvider {

    @Resource(name="userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken AuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;
        // LoginFilter 에서 생성된 토큰으로부터 아이디와 비밀번호를 조회함
        String username = AuthenticationToken.getName();
        String password = (String) AuthenticationToken.getCredentials();

        // UserDetailsService 를 통해 DB에서 username 으로 사용자 조회
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);
        
        // 로그인시 사용한 password와 DB에 저장된 password의 일치 여부 확인
        // password: raw password 암호화 안된 것(요청을 통해 들어온 것)
        // userDetails.getPassword() : DB에 저장되어있는 암호화된 password
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException(userDetails.getUsername() + "Invalid password");
        }
        
        // password일치 -> 인증 성공 -> 인증이 완료된 Authentication 객체 생성 -> LoginSuccessHanlder
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // 인증처리 가능 여부 판단 기준: supports 함수를 통해 "인증정보의 클래스 타입"을 보고 판단
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}