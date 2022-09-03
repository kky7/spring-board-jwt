package com.example.board.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.board.dto.response.ResponseDto;
import com.example.board.entity.Users;
import com.example.board.repository.UserRepository;
import com.example.board.security.UserDetailsImpl;
import com.example.board.security.provider.TokenProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static com.example.board.security.provider.JwtTokenProvider.JwtDecoder;


public class CustomJwtAuthFilter  extends BasicAuthenticationFilter {
    // jwt 복호화해서 확인 - 권한부여하는 인가 필터
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CustomJwtAuthFilter(AuthenticationManager authenticationManager,
                               UserRepository userRepository){
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    public void exceptionResponse(HttpServletResponse response, String code, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ResponseDto<?> responseDto = ResponseDto.fail(code,message);
        String httpResponse = objectMapper.writeValueAsString(responseDto);
        response.getWriter().write(httpResponse);
    }

    @Override
    protected void  doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain chain) throws IOException, ServletException {

        // Header에서 access token 받기
        String jwtHeader = request.getHeader(TokenProperties.AUTH_HEADER);
        String requestUri = request.getRequestURI();

        if(requestUri.contains("auth")){
            if(jwtHeader == null){
                // auth가 포함된 uri는 header 값이 있어야 한다.
                exceptionResponse(response, "INVALID_LOGIN", "로그인이 필요합니다.");
                return;
            }

            if(!jwtHeader.startsWith(TokenProperties.TOKEN_TYPE)){
                exceptionResponse(response,"INVALID_TOKEN","유효하지 않은 Access Token이 입니다.");
                return;
            }

            // access token value
            String accessToken = jwtHeader.replace(TokenProperties.TOKEN_TYPE,"");

            // 복호화
            DecodedJWT decodedJWT = JwtDecoder(accessToken);

            if(decodedJWT == null){
                exceptionResponse(response,"INVALID_TOKEN","유효하지 않은 Access Token이 입니다.");
                return;
            }

            // 서명인, 유효기간 get하기
            String username = decodedJWT.getClaim(TokenProperties.CLAIM_USER_NAME).asString();
            Date expireDate = decodedJWT.getClaim(TokenProperties.CLAIM_EXPIRED_TIME).asDate();
            Date now = new Date();

            if(username == null){
                exceptionResponse(response,"INVALID_TOKEN","유효하지 않은 Access Token이 입니다.");
                return;
            }

            // 유효기간 만료 확인
            if(expireDate.before(now)){
                exceptionResponse(response,"EXPIRED_TOKEN","만료된 Access Token 입니다.");
                return;
            }

            // 권한 부여
            Users user =  userRepository.findByUsername(username);
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,userDetails.getPassword(),userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            System.out.println("권한 부여!");
            chain.doFilter(request,response);

        }else{
            // FilterChain chain 해당 필터가 실행 후 다른 필터도 실행할 수 있도록 연결실켜주는 메서드
            // auth가 포함되지 않은 uri는 인가를 거치지 않고 다음 필터로 넘어간다.
            chain.doFilter(request,response);
        }

    }
    
    // FilterChain : Filter가 여러개 모여서 하나의 체인을 형성하는 것

}
