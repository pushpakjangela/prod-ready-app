package com.pushpak.prod_ready_feature.services;

import com.pushpak.prod_ready_feature.TestContainerConfiguration;
import com.pushpak.prod_ready_feature.dto.PostDto;
import com.pushpak.prod_ready_feature.entities.PostEntity;
import com.pushpak.prod_ready_feature.entities.User;
import com.pushpak.prod_ready_feature.enums.Messages;
import com.pushpak.prod_ready_feature.enums.Permission;
import com.pushpak.prod_ready_feature.enums.Role;
import com.pushpak.prod_ready_feature.exception.ResourceNotFoundException;
import com.pushpak.prod_ready_feature.repositories.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.modelmapper.ModelMapper;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.pushpak.prod_ready_feature.enums.Permission.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
@Import(TestContainerConfiguration.class)
class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModelMapper modelMapper;

    PostEntity postEntity = new PostEntity();

    PostDto postDto = new PostDto();

    @Test
    void tesGetAllPosts() {

        // assign
        when(postRepository.findAll()).thenReturn(List.of(postEntity));
        when(modelMapper.map(postEntity, PostDto.class)).thenReturn(postDto);

        // act
        List<PostDto> result = postService.getAllPosts();

        // assert
        assertThat(result)
                .isNotEmpty()
                .hasSize(1);

        verify(postRepository).findAll();
        verify(modelMapper).map(postEntity, PostDto.class);

    }

    @Test
    void testCreatePost_whenPostIsValid_returnPost() {

        User user = User.builder()
                .id(1L)
                .name("pushpak")
                .email("pushpak.jangela@kfintech.com")
                .password("pushpak@123")
                .roles(Set.of(Role.USER))
                .permissions(Set.of(POST_VIEW, POST_CREATE))
                .build();

        PostDto postDto = PostDto.builder()
                .title("title")
                .description("content")
                .author(user)
                .build();

        PostDto expectedPostDto = PostDto.builder()
                .title("title")
                .description("content")
                .author(user)
                .build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        PostEntity savedPostEntity = new PostEntity();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        SecurityContextHolder.setContext(securityContext);

        when(modelMapper.map(postDto, PostEntity.class)).thenReturn(postEntity);
        when(postRepository.save(postEntity)).thenReturn(savedPostEntity);

        when(modelMapper.map(savedPostEntity, PostDto.class)).thenReturn(expectedPostDto);

        PostDto result = postService.createPost(postDto);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedPostDto);
        assertThat(postEntity.getAuthor())
                .isEqualTo(user);

        verify(modelMapper)
                .map(postDto, PostEntity.class);

        verify(postRepository)
                .save(postEntity);

        verify(modelMapper)
                .map(savedPostEntity, PostDto.class);

        verify(securityContext)
                .getAuthentication();

        verify(authentication)
                .getPrincipal();

    }

    @Test
    void testGetPostById_whenPostExists_returnPost() {

        // Arrange
        Long postId = 1L;

        PostEntity postEntity = new PostEntity();

        PostDto expectedPostDto = PostDto.builder()
                .id(postId)
                .title("title")
                .description("content")
                .build();

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(postEntity));

        when(modelMapper.map(postEntity, PostDto.class))
                .thenReturn(expectedPostDto);

        // Act
        PostDto result = postService.getPostById(postId);

        // Assert
        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedPostDto);

        // Verify
        verify(postRepository)
                .findById(postId);

        verify(modelMapper)
                .map(postEntity, PostDto.class);
    }

    @Test
    void testGetPostById_whenPostDoesNotExist_throwException() {

        // Arrange
        Long postId = 1L;

        when(postRepository.findById(postId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> postService.getPostById(postId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(Messages.POST_NOT_FOUND.getMessage());

        // Verify
        verify(postRepository)
                .findById(postId);

        verifyNoInteractions(modelMapper);
    }
    //
    // @Test
    // void updatePostById() {
    // }
}