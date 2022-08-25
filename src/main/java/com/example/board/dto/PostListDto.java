package com.example.board.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class PostListDto {
    // 작성날짜, 제목, 유저네임
    private Long id;
    private LocalDateTime createdAt;
    private String title;
    private String username;

    public PostListDto(Long id, LocalDateTime createdAt, String title, String username){
        this.id = id;
        this.createdAt = createdAt;
        this.title = title;
        this.username = username;
    }
}

