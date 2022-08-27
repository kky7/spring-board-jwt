package com.example.board.service;

import com.example.board.dto.request.PostListDto;
import com.example.board.dto.request.PostRequestDto;
import com.example.board.dto.response.PostResponseDto;
import com.example.board.dto.response.ResponseDto;
import com.example.board.entity.Post;
import com.example.board.entity.Users;
import com.example.board.repository.PostRepository;
import com.example.board.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Transactional
    public ResponseDto<?> createPost(PostRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();

        Users user = userRepository.findByUsername(username);

        Post post = new Post(requestDto, user);

        postRepository.save(post);

        PostResponseDto postResponseDto = new PostResponseDto(post.getId(), post.getTitle(),post.getContent(),
                username,post.getCreatedAt(),post.getModifiedAt());

        return ResponseDto.success(postResponseDto);
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getPost(Long id) {
        Post post = postRepository.findById(id).orElse(null);

        if (post == null) {
            return ResponseDto.fail("NULL_POST_ID", "post id isn't exist");
        } else{
            PostResponseDto postResponseDto = new PostResponseDto(post.getId(),post.getTitle(),post.getContent(),
                    post.getUser().getUsername(),post.getCreatedAt(),post.getModifiedAt());
            return ResponseDto.success(postResponseDto);
        }

    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost() {
        List<Post> post_list = postRepository.findAllByOrderByModifiedAtDesc();

        List<PostListDto> postListDtos = new ArrayList<>();

        if (!post_list.isEmpty()){
            for(Post post : post_list){
                PostListDto get_post = new PostListDto(post.getId(),post.getCreatedAt(),post.getTitle(),post.getUser().getUsername());

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
