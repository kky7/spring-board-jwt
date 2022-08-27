package com.example.board.security.filter;

// 인증 성공 -> jwt 토큰 발급 -> 로그인 성공
// 아이디와 패스워드를 통해 사이트에 가입된 회원임을, 특정 서비스에 일정 권한이 주어진 사용자임을 인증한다.

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// UsernamePasswordAuthenticationFilter : username, password를 쓰는 form기반 인증을 처리하는 필터
// AuthenticationManager (ProviderManager)를 통한 인증 실행
// 성공하면, Authentication 객체를 SecurityContext에 저장 후 AuthenticationSuccessHandler 실행
// LoginFilter -> LoginAuthProvider -> LoginFilter -> LoginSuccessHandler -> JWT 토큰 생성 -> 응답 헤더에 넣어줌
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    final private ObjectMapper objectMapper;

    public LoginFilter(final AuthenticationManager authenticationManager) {
        super(authenticationManager);
        objectMapper = new ObjectMapper()
                // jackson에서 알지 못하는 프로퍼티 무시
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authRequestToken;
        try {
            JsonNode requestBody = objectMapper.readTree(request.getInputStream());
            // 요청으로부터 username, password를 얻어옴
            String username = requestBody.get("username").asText();
            String password = requestBody.get("password").asText();

            // UernamePasswordAuthenticationToken 객체 생성(인증 토큰) -> Authentication 인터페이스의 구현체
            authRequestToken = new UsernamePasswordAuthenticationToken(username, password);
        } catch (Exception e) {
            throw new RuntimeException("username, password 입력이 필요합니다. (JSON)");
        }

        setDetails(request, authRequestToken);
        // AuthenticationManager의 구현체인 providerManager가  인증을 위임 처리 (여기서 LoginAuthProvider)
        // Filter는 요청한 인증 처리를 할 수  있는 Provider를 찾고, 실제 인증처리는 Provider에 의해 진행됨
        // providerManager의 authenticate(authRequest) : Authentication 객체를 받아 인증하고 인증되었다면 인증된 authentication 객체를 돌려줌
        return this.getAuthenticationManager().authenticate(authRequestToken);
    }
}
