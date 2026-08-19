package com.pushpak.prod_ready_feature.controllers;

import com.pushpak.prod_ready_feature.dto.PostDto;
import com.pushpak.prod_ready_feature.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("getAllPost")
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable Long postId){
        return postService.getPostById(postId);
    }

    @PostMapping("createPost")
    public PostDto createPost(@RequestBody PostDto postDto){
        return postService.createPost(postDto);
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@RequestBody PostDto postDto,@PathVariable Long postId){
        return postService.updatePostById(postDto,postId);
    }

}
