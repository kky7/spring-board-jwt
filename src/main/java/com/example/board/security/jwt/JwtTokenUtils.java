package com.example.board.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.board.security.UserDetailsImpl;

import java.util.Date;

public final class JwtTokenUtils {

    //Access Token 생성
    public static String generateAccessJwtToken(UserDetailsImpl userDetails){

        return JWT.create()
                // 토큰 발급자
                .withIssuer("ky")
                .withClaim(TokenProperties.CLAIM_USER_NAME, userDetails.getUsername())
                .withClaim(TokenProperties.CLAIM_EXPIRED_TIME, new Date(System.currentTimeMillis() + TokenProperties.ACCESS_JWT_TOKEN_VALID_TIME))
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


    private static Algorithm generateAlgorithm() {
        return Algorithm.HMAC256(TokenProperties.JWT_SECRET);
    }
}
