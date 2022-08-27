package com.example.board.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class PostListDto {
    // 작성날짜, 제목, 유저네임
    private Long id;
    private LocalDateTime createdAt;
    private String title;
    private String username;

}

