package com.pushpak.prod_ready_feature.enums;

public enum Messages {

    POST_NOT_FOUND("Post not found with id: %d"),
    USER_NOT_FOUND_WITH_EMAIL("User not found with this %s email"),
    USER_ALREADY_EXISTS_WITH_THIS_EMAIL("User already With this Email"),
    USER_WITH_EMAIL("User With this Email"),
    NOT_FOUND("Not Found");
    private final String message;
    private Messages(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
}
