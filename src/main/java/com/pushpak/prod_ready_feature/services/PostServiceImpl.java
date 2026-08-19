package com.pushpak.prod_ready_feature.services;

import com.pushpak.prod_ready_feature.dto.PostDto;
import com.pushpak.prod_ready_feature.entities.PostEntity;
import com.pushpak.prod_ready_feature.enums.Messages;
import com.pushpak.prod_ready_feature.exception.ResourceNotFoundException;
import com.pushpak.prod_ready_feature.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    @Override
    public List<PostDto> getAllPosts() {
        return postRepository
                .findAll()
                .stream()
                .map(postEntity -> modelMapper.map(postEntity,PostDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        PostEntity postEntity = modelMapper.map(postDto,PostEntity.class);
        return modelMapper.map(postRepository.save(postEntity),PostDto.class);
    }

    @Override
    public PostDto getPostById(Long postId) {
        return modelMapper.map(postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException(Messages.POST_NOT_FOUND.getMessage())), PostDto.class);
    }

    @Override
    public PostDto updatePostById(PostDto inputPostDto, Long postId) {
        PostEntity oldPostEntity = postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException(Messages.POST_NOT_FOUND.getMessage()));
        inputPostDto.setId(postId);
        modelMapper.map(inputPostDto,oldPostEntity);
        PostEntity savedPostEntity = postRepository.save(oldPostEntity);
        return modelMapper.map(savedPostEntity,PostDto.class);
    }
}
