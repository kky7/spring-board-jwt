package com.example.board.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.board.repository.UserRepository;
import com.example.board.security.provider.JwtTokenProvider;
import com.example.board.security.provider.TokenProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
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
    private final JwtTokenProvider jwtTokenProvider;

    public CustomJwtAuthFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider){
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void  doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain chain) throws IOException, ServletException {

        // Header에서 access token 받기
        String jwtHeader = request.getHeader(TokenProperties.AUTH_HEADER);

        // 토큰 존재하는지 확인 없으면 filter chain으로 리턴
        if(jwtHeader == null || !jwtHeader.startsWith(TokenProperties.TOKEN_TYPE)){
            chain.doFilter(request,response);
            return;
        }


        // access token value
        String accessToken = jwtHeader.replace(TokenProperties.TOKEN_TYPE,"");

        // 복호화
        DecodedJWT decodedJWT = JwtDecoder(accessToken);

        // 서명인, 유효기간 get하기
        String username = decodedJWT.getClaim(TokenProperties.CLAIM_USER_NAME).asString();
        Date expireDate = decodedJWT.getClaim(TokenProperties.CLAIM_EXPIRED_TIME).asDate();
        Date now = new Date();
        
        // 유효기간 만료 확인
        if(expireDate.before(now)){
            chain.doFilter(request,response);
            return;
        }

        // 서명인이 있는 경우 권한 부여
        if(username != null){
            Authentication auth = jwtTokenProvider.getAuthentication(username);
            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(request,response);
        }

    }
    
    // FilterChain : Filter가 여러개 모여서 하나의 체인을 형성하는 것

}
