package com.auth.java.controller;

import com.auth.java.exceptions.EmailExistException;
import com.auth.java.exceptions.NotFoundException;
import com.auth.java.exceptions.UserRegistrationExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@Slf4j
public class ExceptionHandler {
    //@ResponseStatus(HttpStatus.NOT_FOUND)
    @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    /*public String handleNotFound(NotFoundException e) {
        return e.getMessage();
    }*/
    public ResponseEntity<?> handleExistEmail(NotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    //@ResponseStatus(HttpStatus.GONE)
    @org.springframework.web.bind.annotation.ExceptionHandler(UserRegistrationExpiredException.class)
    /*public void handExpired(UserRegistrationExpiredException e){
        log.debug("User registration has expired", e);
    }*/
    public ResponseEntity<?> handleExistEmail(UserRegistrationExpiredException e){
        return new ResponseEntity<>("User registration has expired", HttpStatus.GONE);
    }

    //@ResponseStatus(HttpStatus.CONFLICT)
    @org.springframework.web.bind.annotation.ExceptionHandler(EmailExistException.class)
    /*public String handleExistEmail(EmailExistException e){
        return e.getMessage();
    }*/
    public ResponseEntity<?> handleExistEmail(EmailExistException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
}
