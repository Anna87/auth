package com.auth.java.controller;

import com.auth.java.dto.ActivateRequest;
import com.auth.java.dto.JwtAuthenticationResponse;
import com.auth.java.dto.LoginRequest;
import com.auth.java.dto.RegisterRequest;
import com.auth.java.repositories.UserRepository;
import com.auth.java.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {

        authService.registerUser(registerRequest);

        return ResponseEntity.ok("You have been sent an email to confirm registration.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        JwtAuthenticationResponse res = authService.authenticateUser(loginRequest);

        return ResponseEntity.ok(res);
    }

    @PostMapping(value = "/activate")
    public ResponseEntity<?> confirmRegistration(@Valid @RequestBody ActivateRequest activateRequest) throws UnsupportedEncodingException {

        JwtAuthenticationResponse res = authService.activateUser(activateRequest.getToken(), activateRequest.getPassword());

        return ResponseEntity.ok(res);
    }

    @PostMapping(value = "/validateToken")
    public ResponseEntity<?> validateToken(@RequestParam String token)  {

        Boolean res = authService.validateToken(token);

        return ResponseEntity.ok(res);
    }
}
