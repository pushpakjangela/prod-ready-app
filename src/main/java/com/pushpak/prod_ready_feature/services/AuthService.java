package com.pushpak.prod_ready_feature.services;

import com.pushpak.prod_ready_feature.dto.LoginDto;
import com.pushpak.prod_ready_feature.dto.LoginResponseDto;
import com.pushpak.prod_ready_feature.dto.SignUpDto;
import com.pushpak.prod_ready_feature.dto.UserDto;
import com.pushpak.prod_ready_feature.entities.User;
import com.pushpak.prod_ready_feature.enums.Messages;
import com.pushpak.prod_ready_feature.exception.UserAlreadyExistsWithThisEmail;
import com.pushpak.prod_ready_feature.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class  AuthService {
    private  final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;


    public ResponseEntity<UserDto> signUp(SignUpDto signUpDto) {
        Optional<User> user = userRepository.getByEmail(signUpDto.getEmail());
        if(user.isPresent()){
            throw new UserAlreadyExistsWithThisEmail(Messages.USER_ALREADY_EXISTS_WITH_THIS_EMAIL.getMessage());
        }
        User toBeSaved = modelMapper.map(signUpDto,User.class);
        toBeSaved.setPassword(passwordEncoder.encode(signUpDto.getPassword()) );
        User savedCreatedUser = userRepository.save(toBeSaved);
        UserDto userDto = modelMapper.map(savedCreatedUser,UserDto.class);
        return ResponseEntity.ok(userDto);
    }
    public LoginResponseDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword())
        );
        User user =(User) authentication.getPrincipal();
        String accessToken =  jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        sessionService.generateNewSession(user,refreshToken);
        return new LoginResponseDto(user.getId(),accessToken,refreshToken );
    }

    public ResponseEntity<LoginResponseDto> refreshToken(String refreshToken) {
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        sessionService.validateSession(refreshToken);
        User user  = userRepository.getUserById(userId);
        String newAccessToken = jwtService.generateAccessToken(user);
        return ResponseEntity.ok(new LoginResponseDto(user.getId(),newAccessToken,refreshToken));
    }
}
