package com.pushpak.prod_ready_feature.advice;

import com.pushpak.prod_ready_feature.enums.Messages;
import com.pushpak.prod_ready_feature.exception.ResourceNotFoundException;
import com.pushpak.prod_ready_feature.exception.SessionNotFoundException;
import com.pushpak.prod_ready_feature.exception.UserAlreadyExistsWithThisEmail;
import com.pushpak.prod_ready_feature.exception.UserNotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, Messages.POST_NOT_FOUND.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException userNotFoundException){
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, Messages.USER_NOT_FOUND_WITH_EMAIL.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(UserAlreadyExistsWithThisEmail.class)
    public ResponseEntity<ApiError> handleUserAlreadyExistsWithThisEmail(UserAlreadyExistsWithThisEmail userAlreadyExistsWithThisEmail){
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, Messages.USER_ALREADY_EXISTS_WITH_THIS_EMAIL.getMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException authenticationException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, authenticationException.getLocalizedMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtException(JwtException jwtException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, jwtException.getLocalizedMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ApiError> handleSessionNotFoundException(SessionNotFoundException sessionNotFoundException){
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, sessionNotFoundException.getLocalizedMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException accessDeniedException){
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, accessDeniedException.getLocalizedMessage());
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
