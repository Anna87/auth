package com.auth.java.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message){
        super(message);
    }
}
