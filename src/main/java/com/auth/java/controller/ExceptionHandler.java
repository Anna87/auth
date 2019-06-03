package com.auth.java.controller;

import com.auth.java.exceptions.EmailExistException;
import com.auth.java.exceptions.NotFoundException;
import com.auth.java.exceptions.UserRegistrationExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleExistEmail(NotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UserRegistrationExpiredException.class)
    public ResponseEntity<?> handleExistEmail(UserRegistrationExpiredException e){
        return new ResponseEntity<>("User registration has expired", HttpStatus.GONE);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(EmailExistException.class)
    public ResponseEntity<?> handleExistEmail(EmailExistException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
}
