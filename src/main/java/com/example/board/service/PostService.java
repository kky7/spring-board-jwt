package com.example.board.service;

import com.example.board.dto.PostListDto;
import com.example.board.dto.PostRequestDto;
import com.example.board.dto.ResponseDto;
import com.example.board.dto.passwordDto;
import com.example.board.entity.Post;
import com.example.board.repository.PostRepository;
import com.example.board.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public ResponseDto<?> createPost(PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        String username = userDetails.getUsername();
        Long userId = userDetails.getUserId();

        Post post = new Post(requestDto, username, userId);

        postRepository.save(post);

        return ResponseDto.success(post);
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);

        if (optionalPost.isEmpty()) {
            return ResponseDto.fail("NULL_POST_ID", "post id isn't exist");
        }

        return ResponseDto.success(optionalPost.get());
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost() {
        List<Post> post_list = postRepository.findAllByOrderByModifiedAtDesc();

        List<PostListDto> postListDtos = new ArrayList<>();

        if (!post_list.isEmpty()){
            for(Post post : post_list){
                PostListDto get_post = new PostListDto(post.getId(),post.getCreatedAt(),post.getTitle(),post.getUsername());

                postListDtos.add(get_post);
            }
        }

        return ResponseDto.success(postListDtos);
    }
//
//    @Transactional
//    public ResponseDto<Post> updatePost(Long id, PostRequestDto requestDto) {
//        Optional<Post> optionalPost = postRepository.findById(id);
//
//        if (optionalPost.isEmpty()) {
//            return ResponseDto.fail("NULL_POST_ID", "post id isn't exist");
//        }
//
//        Post post = optionalPost.get();
//        post.update(requestDto);
//
//        return ResponseDto.success(post);
//    }
//
//    @Transactional
//    public ResponseDto<?> deletePost(Long id) {
//        Optional<Post> optionalPost = postRepository.findById(id);
//
//        if (optionalPost.isEmpty()) {
//            return ResponseDto.fail("NOT_FOUND", "post id is not exist");
//        }
//
//        Post post = optionalPost.get();
//
//        postRepository.delete(post);
//
//        return ResponseDto.success(true);
//    }


}
