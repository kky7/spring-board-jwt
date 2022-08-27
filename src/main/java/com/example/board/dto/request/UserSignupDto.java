package com.example.board.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignupDto {
    private String username;
    private String password;
    private String passwordConfirm;
}
