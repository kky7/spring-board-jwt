package com.example.board.security;

import com.example.board.dto.ResponseDto;
import com.example.board.entity.RefreshToken;
import com.example.board.entity.Users;
import com.example.board.repository.RefreshTokenRepository;
import com.example.board.repository.UserRepository;
import com.example.board.security.provider.JwtTokenProvider;
import com.example.board.security.jwt.TokenProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


// 로그인 성공시 호출
// 인증 성공시 handler
// JWT Token 생성
@NoArgsConstructor
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        // UserDetails를 구현한 사용자 객체를 반환 -> UserDetails를 구현한 객체가 가지고 있는 정보를 가져올 수 있음
        final UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();


        //AccessToken 생성
        final String accessToken = JwtTokenProvider.generateAccessJwtToken(userDetails);
        //RefreshToken 생성
        final String refreshToken = JwtTokenProvider.generateRefreshJwtToken(userDetails);


        // Response 헤더에 Access, Refresh 토큰 담아줌
        response.addHeader(TokenProperties.AUTH_HEADER, TokenProperties.TOKEN_TYPE + accessToken);
        response.addHeader(TokenProperties.REFRESH_HEADER, TokenProperties.TOKEN_TYPE + refreshToken);
        response.addHeader("Access-Token-Valid-Time", Integer.toString(TokenProperties.ACCESS_JWT_TOKEN_VALID_TIME));

        Users user = userDetails.getUser();
        RefreshToken refreshTokenObject = RefreshToken.builder()
                .id(user.getId())
                .users(user)
                .tokenValue(refreshToken)
                .build();

        refreshTokenRepository.save(refreshTokenObject);

        // json형식으로 response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ResponseDto<Users> responseDto = new ResponseDto<>(true, user, null);
        String result = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(responseDto);
        response.getWriter().write(result);
    }

}