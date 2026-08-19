package com.pushpak.prod_ready_feature.exception;

public class UserAlreadyExistsWithThisEmail extends RuntimeException {
    public UserAlreadyExistsWithThisEmail(String message) {
        super(message);
    }
}
