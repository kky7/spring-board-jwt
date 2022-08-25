package com.example.board.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.board.security.UserDetailsImpl;

import java.util.Date;

public final class JwtTokenUtils {

    private static final int SEC = 1;
    private static final int MINUTE = 60 * SEC;
//    private static final int HOUR = 60 * MINUTE;
//    private static final int DAY = 24 * HOUR;


    // Access JWT 토큰의 유효기간: 30분 (단위: milliseconds)
    private static final int ACCESS_JWT_TOKEN_VALID_TIME = 30* MINUTE * 1000;

    // Refresh JWT 토큰의 유효기간: 50분 (단위: milliseconds)
    private static final int REFRESH_JWT_TOKEN_VALID_TIME = 50 * MINUTE * 1000;

    public static final String CLAIM_EXPIRED_TIME = "EXPIRED_TIME";
    public static final String CLAIM_USER_NAME = "USER_NAME";
    public static final String JWT_SECRET = "kyblog"; // 시크릿 키. 노출되면 안됨 .이걸로 로그인 정보 암호화, JWT 위조검증

    //Access Token 생성
    public static String generateAccessJwtToken(UserDetailsImpl userDetails){

        return JWT.create()
                .withIssuer("ky")
                .withClaim(CLAIM_USER_NAME, userDetails.getUsername())
                .withClaim(CLAIM_EXPIRED_TIME, new Date(System.currentTimeMillis() + ACCESS_JWT_TOKEN_VALID_TIME))
                .sign(generateAlgorithm());

    }
    
    // Refresh Token 생성
    public static String generateRefreshJwtToken(UserDetailsImpl userDetails){

        return JWT.create()
                .withIssuer("ky")
                .withClaim(CLAIM_USER_NAME, userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_JWT_TOKEN_VALID_TIME))
                .sign(generateAlgorithm());

    }


    private static Algorithm generateAlgorithm() {
        return Algorithm.HMAC256(JWT_SECRET);
    }
}
