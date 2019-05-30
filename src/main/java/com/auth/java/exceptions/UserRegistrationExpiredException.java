package com.auth.java.exceptions;

public class UserRegistrationExpiredException extends RuntimeException {
    public UserRegistrationExpiredException() {
        super("User registration token has expired");
    }
}
