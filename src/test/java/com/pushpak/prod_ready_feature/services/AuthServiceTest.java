package com.pushpak.prod_ready_feature.services;

import com.pushpak.prod_ready_feature.TestContainerConfiguration;
import com.pushpak.prod_ready_feature.dto.LoginDto;
import com.pushpak.prod_ready_feature.dto.LoginResponseDto;
import com.pushpak.prod_ready_feature.dto.SignUpDto;
import com.pushpak.prod_ready_feature.dto.UserDto;
import com.pushpak.prod_ready_feature.entities.User;
import com.pushpak.prod_ready_feature.enums.Messages;
import com.pushpak.prod_ready_feature.enums.Permission;
import com.pushpak.prod_ready_feature.enums.Role;
import com.pushpak.prod_ready_feature.exception.UserAlreadyExistsWithThisEmail;
import com.pushpak.prod_ready_feature.repositories.PostRepository;
import com.pushpak.prod_ready_feature.repositories.UserRepository;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
 import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration .class)
@ExtendWith(MockitoExtension.class)

class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private  AuthenticationManager authenticationManager;

    @Mock
    private  JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private  PasswordEncoder passwordEncoder;

    @Mock
    private  SessionService sessionService;


    @Mock
    private  PostRepository postRepository;

    @Spy
    private  ModelMapper modelMapper;

    @Mock
    private User user,loginUser2;

    private SignUpDto signUpDto;

    private LoginDto loginDto;



    Long id = 7L;

    @BeforeEach
    void loginDtoSetUp(){
        loginDto = LoginDto.builder()
                .email("pranjal@gmail.com")
                .password("pranjal@12345")
                .build();
    }

    @BeforeEach
    void loginUserSetup(){
            loginUser2 = User.builder()
                    .id(4L)
                    .name("Pranjal")
                    .email("pranjal@gmail.com")
                    .roles(Set.of(Role.USER,Role.CREATOR))
                    .permissions(Set.of(Permission.POST_CREATE,Permission.POST_VIEW))
                    .password("encodedPassword")
                    .build();
    }
    @BeforeEach
    void setUp() {
        signUpDto = SignUpDto.builder()
                .name("Pranjal")
                .email("pranjal@gmail.com")
                .roles(Set.of(Role.USER,Role.CREATOR))
                .permissions(Set.of(Permission.POST_CREATE,Permission.POST_VIEW))
                .password("pranjal@12345")
                .build();
    }

    String refreshToken = "refresh-token";



    @Test
    void testSignUpUser_whenUserIsValid_returnUser() {

        // Arrange

        when(userRepository.getByEmail(signUpDto.getEmail()))
                .thenReturn(Optional.empty());

        when(modelMapper.map(signUpDto, User.class))
                .thenReturn(user);

        when(passwordEncoder.encode(signUpDto.getPassword()))
                .thenReturn("encodedPassword");

        when(userRepository.save(user))
                .thenReturn(user);

        @Nullable UserDto userDto = null;
        when(modelMapper.map(user, UserDto.class))
                .thenReturn(userDto);


        // Act

        ResponseEntity<UserDto> response =
                authService.signUp(signUpDto);


        // Assert

        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
                .isEqualTo(userDto);

        verify(userRepository)
                .getByEmail(signUpDto.getEmail());

        verify(modelMapper)
                .map(signUpDto, User.class);

        verify(passwordEncoder)
                .encode(signUpDto.getPassword());

        verify(userRepository)
                .save(user);

        verify(modelMapper)
                .map(user, UserDto.class);
    }
    @Test
    void testSignUpUser_whenUserIsAlready_present_returnException() {
        when(userRepository.getByEmail(signUpDto.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.signUp(signUpDto))
                .isInstanceOf(UserAlreadyExistsWithThisEmail.class)
                .hasMessage(Messages.USER_ALREADY_EXISTS_WITH_THIS_EMAIL.getMessage());

        verify(userRepository).getByEmail(signUpDto.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSignInUser_whenUserIsValid_returnUser() {
    }
    @Test
    void testLogin_whenUserIsValid_returnUser() {

        // =========================
        // ARRANGE
        // =========================

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(loginUser2);

        when(jwtService.generateAccessToken(loginUser2))
                .thenReturn("access-token");

        when(jwtService.generateRefreshToken(loginUser2))
                .thenReturn("refresh-token");


        // =========================
        // ACT
        // =========================

        LoginResponseDto loginResponseDto =
                authService.login(loginDto);


        // =========================
        // ASSERT
        // =========================

        assertThat(loginResponseDto)
                .isNotNull();

        assertThat(loginResponseDto.getId())
                .isEqualTo(loginUser2.getId());

        assertThat(loginResponseDto.getAccessToken())
                .isEqualTo("access-token");

        assertThat(loginResponseDto.getRefreshToken())
                .isEqualTo("refresh-token");


        // =========================
        // CAPTURE AUTH TOKEN
        // =========================

        ArgumentCaptor<UsernamePasswordAuthenticationToken>
                authenticationCaptor =
                ArgumentCaptor.forClass(
                        UsernamePasswordAuthenticationToken.class
                );

        verify(authenticationManager)
                .authenticate(authenticationCaptor.capture());

        UsernamePasswordAuthenticationToken authenticationToken =
                authenticationCaptor.getValue();


        // =========================
        // VERIFY EMAIL + PASSWORD
        // =========================

        assertThat(authenticationToken.getPrincipal())
                .isEqualTo(loginDto.getEmail());

        assertThat(authenticationToken.getCredentials())
                .isEqualTo(loginDto.getPassword());


        // =========================
        // VERIFY OTHER DEPENDENCIES
        // =========================

        verify(authentication)
                .getPrincipal();

        verify(jwtService)
                .generateAccessToken(loginUser2);

        verify(jwtService)
                .generateRefreshToken(loginUser2);

        verify(sessionService)
                .generateNewSession(
                        loginUser2,
                        "refresh-token"
                );
    }

    @Test
    void testRefreshToken_whenTokenIsValid_returnUser_generateNewSession() {

        //assign
        when(jwtService.getUserIdFromToken(refreshToken)).thenReturn(id);

       doNothing().when(sessionService).validateSession(refreshToken);

       when(userRepository.getUserById(id)).thenReturn(loginUser2);
       when(jwtService.generateAccessToken(loginUser2)).thenReturn("new-access-token");

       // act
        ResponseEntity<LoginResponseDto> response = authService.refreshToken(refreshToken);
       // assert
        assertThat(response).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(loginUser2.getId());
        assertThat(response.getBody().getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getBody().getRefreshToken()).isEqualTo(refreshToken);


        verify(jwtService)
                .getUserIdFromToken(refreshToken);

        verify(sessionService)
                .validateSession(refreshToken);

        verify(userRepository)
                .getUserById(id);

        verify(jwtService)
                .generateAccessToken(loginUser2);
    }
}