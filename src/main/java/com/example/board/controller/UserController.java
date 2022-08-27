package com.example.board.controller;

import com.example.board.dto.ResponseDto;
import com.example.board.dto.UserLoginDto;
import com.example.board.dto.UserSignupDto;
import com.example.board.security.UserDetailsImpl;
import com.example.board.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/board/member")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    // 로그인
//    @PostMapping("/login") -> 없어도 됨
//    public ResponseDto<?> login(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request){
//        return userService.login(userLoginDto, request);
//    }

    //회원가입 - 모두 접근 가능
    @PostMapping("/signup")
    public ResponseDto<?> signup(@RequestBody UserSignupDto userSignupDto) {
        return userService.signup(userSignupDto);
    }

     //로그 아웃
//    @GetMapping("/logout")
//    public ResponseDto<?> logout(@AuthenticationPrincipal UserDetailsImpl userDetails){
//        return userService.logout(userDetails);
//    }

}
