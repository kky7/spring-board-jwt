package com.example.board.security;

import com.example.board.dto.ResponseDto;
import com.example.board.entity.Users;
import com.example.board.repository.UserRepository;
import com.example.board.security.jwt.JwtTokenUtils;
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
    public static final String AUTH_HEADER = "authorization";
    public static final String REFRESH_HEADER = "Refresh-Token";
    public static final String TOKEN_TYPE = "BEARER ";


    @Autowired
    private UserRepository userRepository;

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
        final String accessToken = JwtTokenUtils.generateAccessJwtToken(userDetails);
        //RefreshToken 생성
        final String refreshToken = JwtTokenUtils.generateRefreshJwtToken(userDetails);


        // Response 헤더에 Access, Refresh 토큰 담아줌
        response.addHeader(AUTH_HEADER, TOKEN_TYPE + accessToken);
        response.addHeader(REFRESH_HEADER, TOKEN_TYPE + refreshToken);

        Users user = userDetails.getUser();
        //Refresh Token DB에 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // json형식으로 response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ResponseDto<Users> responseDto = new ResponseDto<>(true, user, null);
        String result = objectMapper.registerModule(new JavaTimeModule()).writeValueAsString(responseDto);
        response.getWriter().write(result);
    }

}