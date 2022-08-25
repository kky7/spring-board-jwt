package com.example.board.controller;

import com.example.board.dto.PostRequestDto;
import com.example.board.dto.ResponseDto;
import com.example.board.dto.passwordDto;
import com.example.board.security.UserDetailsImpl;
import com.example.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/board")
public class PostController {

    private final PostService postService;

    @PostMapping("/auth/post")
    public ResponseDto<?> createPost(@RequestBody PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.createPost(requestDto, userDetails);
    }

    @GetMapping("/post/{id}")
    public ResponseDto<?> getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @GetMapping("/post")
    public ResponseDto<?> getAllPosts() {
        return postService.getAllPost();
    }
//
//    @PutMapping("/post/{id}")
//    public ResponseDto<?> updatePost(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto) {
//        return postService.updatePost(id, postRequestDto);
//    }
//
//    @DeleteMapping("/post/{id}")
//    public ResponseDto<?> deletePost(@PathVariable Long id) {
//        return postService.deletePost(id);
//    }

}
