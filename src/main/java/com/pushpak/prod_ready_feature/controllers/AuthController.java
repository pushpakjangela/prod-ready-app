package com.pushpak.prod_ready_feature.controllers;

import com.pushpak.prod_ready_feature.dto.LoginDto;
import com.pushpak.prod_ready_feature.dto.LoginResponseDto;
import com.pushpak.prod_ready_feature.dto.SignUpDto;
import com.pushpak.prod_ready_feature.dto.UserDto;
import com.pushpak.prod_ready_feature.services.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class  AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpDto signUpDto) {
         return authService.signUp(signUpDto);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpServletRequest request, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = authService.login(loginDto);
        Cookie cookie = new Cookie("refreshToken", loginResponseDto.getRefreshToken());
        response.addCookie( cookie);
        return ResponseEntity.ok(loginResponseDto);
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
      String refreshToken = Arrays.stream(request.getCookies())
              .filter(cookie -> "refreshToken".equals(cookie.getName()))
              .findFirst()
              .map(Cookie::getValue)
              .orElseThrow(()-> new AuthenticationServiceException("refresh token not found inside cookies"));
      return authService.refreshToken(refreshToken);
    }

}
