package com.pushpak.prod_ready_feature.controllers;

import com.pushpak.prod_ready_feature.TestContainerConfiguration;
import com.pushpak.prod_ready_feature.dto.LoginDto;
import com.pushpak.prod_ready_feature.dto.LoginResponseDto;
import com.pushpak.prod_ready_feature.dto.PostDto;
import com.pushpak.prod_ready_feature.entities.User;
import com.pushpak.prod_ready_feature.enums.Permission;
import com.pushpak.prod_ready_feature.enums.Role;
import com.pushpak.prod_ready_feature.repositories.PostRepository;
import com.pushpak.prod_ready_feature.repositories.SessionRepository;
import com.pushpak.prod_ready_feature.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureRestTestClient
@Import(TestContainerConfiguration.class)
class PostControllerTest {

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    private static final String EMAIL = "jangela@gmail.com";
    private static final String PASSWORD = "JANGELA@123";


    // =========================================================
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() {

        postRepository.deleteAll();
        sessionRepository.deleteAll();
        userRepository.deleteAll();

        user = User.builder()
                .name("JANGELA")
                .email(EMAIL)

                // IMPORTANT:
                // Store BCrypt encoded password in DB
                .password(passwordEncoder.encode(PASSWORD))

                .roles(Set.of(Role.USER))
                .permissions(Set.of(
                        Permission.POST_VIEW,
                        Permission.POST_CREATE
                ))
                .build();

        user = userRepository.save(user);
    }


    // =========================================================
    // GET JWT TOKEN
    // =========================================================

    private String getAccessToken() {

        LoginDto loginDto = LoginDto.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        LoginResponseDto response =
                restTestClient
                        .post()
                        .uri("/auth/login")
                        .body(loginDto)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(LoginResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(response);

        return response.getAccessToken();
    }


    // =========================================================
    // GET ALL POSTS
    // =========================================================

    @Test
    void testGetAllPostsControllerIT() {

        String token = getAccessToken();

        List<PostDto> posts =
                restTestClient
                        .get()
                        .uri("/posts/getAllPost")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(new ParameterizedTypeReference<List<PostDto>>() {})
                        .returnResult()
                        .getResponseBody();

        assertNotNull(posts);
    }


    // =========================================================
    // GET ALL POSTS WITHOUT AUTHENTICATION
    // =========================================================

    @Test
    void testGetAllPostsWithoutAuthenticationControllerIT() {

        restTestClient
                .get()
                .uri("/posts/getAllPost")
                .exchange()
                .expectStatus()
                .isForbidden();
    }


    // =========================================================
    // CREATE POST
    // =========================================================

    @Test
    void testCreatePostControllerIT() {

        String token = getAccessToken();

        PostDto request = PostDto.builder()
                .title("Test Post")
                .description("Integration Test Description")
                .build();

        restTestClient
                .post()
                .uri("/posts/createPost")
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .body(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(PostDto.class)
                .consumeWith(response -> {

                    PostDto result =
                            response.getResponseBody();

                    assertNotNull(result);
                    assertNotNull(result.getId());

                    assertEquals(
                            "Test Post",
                            result.getTitle()
                    );

                    assertEquals(
                            "Integration Test Description",
                            result.getDescription()
                    );
                });
    }


    // =========================================================
    // GET POST BY ID
    // =========================================================

    @Test
    void testGetPostByIdControllerIT() {

        String token = getAccessToken();

        /*
         * Create the post through the API.
         *
         * This is important because your controller has:
         *
         * @PreAuthorize("@postSecurity.isPostOwner(#postId)")
         *
         * Therefore the post must belong to the authenticated user.
         */

        PostDto createRequest = PostDto.builder()
                .title("Owner Post")
                .description("Owner Test")
                .build();

        PostDto createdPost =
                restTestClient
                        .post()
                        .uri("/posts/createPost")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .body(createRequest)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(PostDto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(createdPost);
        assertNotNull(createdPost.getId());

        // Now get the post
        restTestClient
                .get()
                .uri(
                        "/posts/{postId}",
                        createdPost.getId()
                )
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(PostDto.class)
                .consumeWith(response -> {

                    PostDto result =
                            response.getResponseBody();

                    assertNotNull(result);

                    assertEquals(
                            "Owner Post",
                            result.getTitle()
                    );

                    assertEquals(
                            "Owner Test",
                            result.getDescription()
                    );
                });
    }


    // =========================================================
    // GET POST BY ID - NOT FOUND
    // =========================================================

    @Test
    void testGetPostByIdNotFoundControllerIT() {

        String token = getAccessToken();

        restTestClient
                .get()
                .uri(
                        "/posts/{postId}",
                        999999L
                )
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .exchange()
                .expectStatus()
                .isNotFound();
    }


    // =========================================================
    // UPDATE POST
    // =========================================================

    @Test
    void testUpdatePostControllerIT() {

        String token = getAccessToken();

        // Create post first
        PostDto createRequest = PostDto.builder()
                .title("Old Title")
                .description("Old Description")
                .build();

        PostDto createdPost =
                restTestClient
                        .post()
                        .uri("/posts/createPost")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .body(createRequest)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(PostDto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(createdPost);
        assertNotNull(createdPost.getId());


        // Update the post
        PostDto updateRequest = PostDto.builder()
                .title("Updated Title")
                .description("Updated Description")
                .build();

        restTestClient
                .put()
                .uri(
                        "/posts/{postId}",
                        createdPost.getId()
                )
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .body(updateRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(PostDto.class)
                .consumeWith(response -> {

                    PostDto result =
                            response.getResponseBody();

                    assertNotNull(result);

                    assertEquals(
                            "Updated Title",
                            result.getTitle()
                    );

                    assertEquals(
                            "Updated Description",
                            result.getDescription()
                    );
                });
    }


    // =========================================================
    // UPDATE POST - NOT FOUND
    // =========================================================

    @Test
    void testUpdatePostNotFoundControllerIT() {

        String token = getAccessToken();

        PostDto updateRequest = PostDto.builder()
                .title("Updated Title")
                .description("Updated Description")
                .build();

        restTestClient
                .put()
                .uri(
                        "/posts/{postId}",
                        999999L
                )
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .body(updateRequest)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}