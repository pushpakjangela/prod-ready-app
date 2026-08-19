package com.pushpak.prod_ready_feature.services;

import com.pushpak.prod_ready_feature.dto.PostDto;

import java.util.List;

public interface PostService {

    List<PostDto> getAllPosts();
    PostDto createPost(PostDto postDto);

    PostDto getPostById(Long postId);

    PostDto updatePostById(PostDto postDto, Long postId);
}
