package com.example.board.security.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.board.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import java.util.Date;

@RequiredArgsConstructor
public class JwtTokenProvider {

    // 사용 알고리즘
    private static Algorithm generateAlgorithm() {
        return Algorithm.HMAC256(TokenProperties.JWT_SECRET);
    }

    //Access Token 생성
    public static String generateAccessJwtToken(UserDetailsImpl userDetails){

        return JWT.create()
                // 토큰 발급자
                .withIssuer("ky")
                // user name으로 서명 
                .withClaim(TokenProperties.CLAIM_USER_NAME, userDetails.getUsername())
                // 기한 설정
                .withClaim(TokenProperties.CLAIM_EXPIRED_TIME, new Date(System.currentTimeMillis() + TokenProperties.ACCESS_JWT_TOKEN_VALID_TIME))
                // 해당 알고리즘으로 생성
                .sign(generateAlgorithm());

    }
    
    // Refresh Token 생성
    public static String generateRefreshJwtToken(UserDetailsImpl userDetails){

        return JWT.create()
                .withIssuer("ky")
                .withClaim(TokenProperties.CLAIM_USER_NAME, userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + TokenProperties.REFRESH_JWT_TOKEN_VALID_TIME))
                .sign(generateAlgorithm());

    }


    // jwt token 복호화
    public static DecodedJWT JwtDecoder(String TokenStringValue) {

        System.out.println(TokenStringValue);

        DecodedJWT decodedJWT = null;

        try {
            Algorithm algorithm = Algorithm.HMAC256(TokenProperties.JWT_SECRET);
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            decodedJWT = jwtVerifier.verify(TokenStringValue);
        } catch (Exception e) {
            throw new IllegalArgumentException("유효한 토큰이 아닙니다.");
        }

        return decodedJWT;

    }

}
