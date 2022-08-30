package com.example.board.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.board.dto.response.ResponseDto;
import com.example.board.dto.request.UserLoginDto;
import com.example.board.dto.request.UserSignupDto;
import com.example.board.entity.Users;
import com.example.board.repository.RefreshTokenRepository;
import com.example.board.repository.UserRepository;
import com.example.board.security.UserDetailsImpl;
import com.example.board.security.provider.TokenProperties;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.regex.Pattern;

import static com.example.board.security.provider.JwtTokenProvider.JwtDecoder;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private  boolean isInUserCheck(String username){
        Users user = userRepository.findByUsername(username);

        return user == null;
    }

    private boolean userStrCheck (String username){
        return Pattern.matches("^[a-zA-Z0-9]{4,12}$", username);
    }

    private boolean passwordStrCheck(String password){
        return Pattern.matches("^[a-z0-9]{4,32}$", password);
    }

    private  boolean isSamePassword(String password, String ConfirmPassword){
        return password.equals(ConfirmPassword);
    }

    //회원가입
    @Transactional
    public ResponseDto<?> signup(UserSignupDto userSignupDto) {
        String username = userSignupDto.getUsername();
        UserLoginDto userLoginDto = new UserLoginDto();

        String password = userSignupDto.getPassword();
        String ConfirmPassword = userSignupDto.getPasswordConfirm();
//        System.out.println(password);

        if (!isInUserCheck(username)) {return ResponseDto.fail("NICKNAME EXISTS", "user name is duplicated");}

        else if (!userStrCheck(username)) {return ResponseDto.fail("WRONG NICKNAME", "user name must consist of 4 or more, 12 or less alphabetic uppercase and lowercase letters(a~z, A~Z) and numbers (0~9)");}

        else if (!passwordStrCheck(password)) {return ResponseDto.fail("WRONG PASSWORD", "password must consist of 4 or more, 32 or less alphabetic lowercase letters(a~z) and numbers (0~9)");}

        else if (!isSamePassword(password, ConfirmPassword)) {return ResponseDto.fail("CHECK PASSWORD", "passwords are not the same.");}

        else {
            userLoginDto.setPassword(passwordEncoder.encode(password));
            userLoginDto.setUsername(username);

            Users user = new Users(userLoginDto);

            userRepository.save(user);

            return ResponseDto.success(user);
        }
    }

    @Transactional
    public ResponseDto<?> logout(UserDetailsImpl userDetails){
        System.out.println("logout");
        Users user = userDetails.getUser();
        System.out.println(user.getId());
        refreshTokenRepository.findByUsers(user).ifPresent(refreshTokenRepository::delete);

        return ResponseDto.success(null);
    }

    @Transactional
    public ResponseDto<?> createNewAccessToken(HttpServletRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // access 토큰, refresh token 헤더에 받음
        String accessToken = request.getHeader(TokenProperties.AUTH_HEADER);
        String refreshToken = request.getHeader(TokenProperties.REFRESH_HEADER);

        // refresh 토큰 겅증
        DecodedJWT decodedRefreshToken = JwtDecoder(refreshToken);
        String username = decodedRefreshToken.getClaim(TokenProperties.CLAIM_USER_NAME).asString();

//        Users user

        // access 토큰 생성
        
        // 새로 발급한 aceess 토근 헤더에 응답

        return ResponseDto.success("success");
    }
}
