package com.example.board.controller;

import com.example.board.dto.request.PostRequestDto;
import com.example.board.dto.response.ResponseDto;
import com.example.board.security.UserDetailsImpl;
import com.example.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

    @PutMapping("/auth/post/{id}")
    public ResponseDto<?> updatePost(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.updatePost(id, postRequestDto, userDetails);
    }

    @DeleteMapping("/auth/post/{id}")
    public ResponseDto<?> deletePost(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.deletePost(id, userDetails);
    }

}
