package com.example.board.security.provider;

public interface TokenProperties {
    String AUTH_HEADER = "Authorization";
    String REFRESH_HEADER = "Refresh-Token";
    String TOKEN_TYPE = "BEARER ";

    // Access JWT 토큰의 유효기간: 30분 (단위: milliseconds)
//    int ACCESS_JWT_TOKEN_VALID_TIME = 30 * 60 * 1000;
    int ACCESS_JWT_TOKEN_VALID_TIME = 2 * 60 * 1000;

    // Refresh JWT 토큰의 유효기간: 하루 (단위: milliseconds)
    int REFRESH_JWT_TOKEN_VALID_TIME = 24 * 60 * 60 * 1000;

    String CLAIM_EXPIRED_TIME = "EXPIRED_TIME";
    String CLAIM_USER_NAME = "USER_NAME";


    String JWT_SECRET = "ky_blog";
}
