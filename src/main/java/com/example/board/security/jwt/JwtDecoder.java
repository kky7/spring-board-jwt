package com.example.board.security.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtDecoder {


    public static final String JWT_SECRET = "kyblog"; // 시크릿 키. 노출되면 안됨 .이걸로 로그인 정보 암호화, JWT 위조검증

    //decode
    public static DecodedJWT decodeAccessJwtToken(String jwtTokenString){

        DecodedJWT decodedJWT = null;

        try{
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            decodedJWT = jwtVerifier.verify(jwtTokenString);
        } catch (Exception e){
            throw new IllegalArgumentException("유효한 토큰이 아닙니다.");
        }

        return decodedJWT;

    }

}

