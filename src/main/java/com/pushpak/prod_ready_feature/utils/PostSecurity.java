package com.pushpak.prod_ready_feature.utils;

import com.pushpak.prod_ready_feature.dto.PostDto;
import com.pushpak.prod_ready_feature.entities.User;
import com.pushpak.prod_ready_feature.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostSecurity {
    private final PostService postService;
    public boolean isPostOwner(Long postId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PostDto post = postService.getPostById(postId);
        return post.getAuthor().getId().equals(user.getId());
    }
}
