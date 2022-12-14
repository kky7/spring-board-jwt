package com.example.board.controller;

import com.example.board.dto.response.ResponseDto;
import com.example.board.dto.request.UserSignupDto;
import com.example.board.security.UserDetailsImpl;
import com.example.board.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    // 로그인 맵핑이 없어도 된다.
    // Form login을 사용하여 인증을 한다.
    // -> UsernamePasswordAuthenticationFilter를 이용하여 인증을 한다.
    // UsernamePasswordAuthenticationFilter를 상속받는 Login Filter에서  HttpServletRequest request를 통해 username과 password를 받는다.
    // "/login" uri를 통해서만 로그인이 되도록 정해져 있기 때문에 loginFilter.setFilterProcessesUrl("/board/member/login"); 으로 로그인 uri를 지정해주어야 한다.

    //회원가입 - 모두 접근 가능
    @PostMapping("/board/member/signup")
    public ResponseDto<?> signup(@RequestBody UserSignupDto userSignupDto) {
        return userService.signup(userSignupDto);
    }

     //로그 아웃
    @PostMapping("/board/auth/member/logout")
    public ResponseDto<?> logout(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return userService.logout(userDetails);
    }

    @PostMapping("/auth/reissue")
    public ResponseDto<?> createNewAccessToken(HttpServletRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.createNewAccessToken(request, userDetails);
    }

}
