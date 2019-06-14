package com.auth.java.controller;

import com.auth.java.dto.requests.ActivateRequest;
import com.auth.java.dto.responses.JwtAuthenticationResponse;
import com.auth.java.dto.requests.LoginRequest;
import com.auth.java.dto.requests.RegisterRequest;
import com.auth.java.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public void registerUser(@Valid @RequestBody final RegisterRequest registerRequest) {
        authService.registerUser(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody final LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping(value = "/activate")
    public ResponseEntity<JwtAuthenticationResponse> confirmRegistration(@Valid @RequestBody final ActivateRequest activateRequest) {
        return ResponseEntity.ok(authService.activateUser(activateRequest.getToken(), activateRequest.getPassword()));
    }

    @PostMapping(value = "/validate")
    public ResponseEntity<Boolean> validateToken(@NotBlank @RequestParam final String token)  {
        return ResponseEntity.ok(authService.validateToken(token));
    }
}
