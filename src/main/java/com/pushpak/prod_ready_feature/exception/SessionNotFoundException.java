package com.pushpak.prod_ready_feature.exception;

public class SessionNotFoundException extends RuntimeException {
  public SessionNotFoundException(String message) {
    super(message);
  }
}
