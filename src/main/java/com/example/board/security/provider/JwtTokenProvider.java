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
//    private static final ObjectMapper objectMapper = new ObjectMapper();

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

        DecodedJWT decodedJWT = null;

        Algorithm algorithm = Algorithm.HMAC256(TokenProperties.JWT_SECRET);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        decodedJWT = jwtVerifier.verify(TokenStringValue);

        return decodedJWT;

    }

//        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
//        Claims claims;
//        try {
//            claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
//        } catch (ExpiredJwtException e) {
//            claims = e.getClaims();
//        }
//
//        if (claims.getExpiration().toInstant().toEpochMilli() < Instant.now().toEpochMilli()) {
//            response.setContentType("application/json;charset=UTF-8");
//            response.getWriter().println(
//                    new ObjectMapper().writeValueAsString(
//                            ResponseDto.fail("BAD_REQUEST", "Token이 유효햐지 않습니다.")
//                    )
//            );
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        }

}
