package com.auth.java.exceptions;


public class EmailExistException extends RuntimeException {
    public EmailExistException(){
        super("Email address already in use.");
    }
}
