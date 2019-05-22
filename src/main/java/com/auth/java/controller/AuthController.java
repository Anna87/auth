package com.auth.java.controller;

import com.auth.java.model.RoleName;
import com.auth.java.model.User;
import com.auth.java.payload.ApiResponse;
import com.auth.java.payload.JwtAuthenticationResponse;
import com.auth.java.payload.LoginRequest;
import com.auth.java.payload.SignUpRequest;
import com.auth.java.repositories.RoleRepository;
import com.auth.java.repositories.UserRepository;
import com.auth.java.security.JwtTokenProvider;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        /*if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }*/

        Optional<User> userOptional = userRepository.findByEmail(signUpRequest.getEmail());

        if (userOptional.isPresent()) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        // Creating user's account
        User newUser = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .roles(Sets.newHashSet(RoleName.ROLE_USER.name()))
                .build();

        User result = userRepository.save(newUser);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        //SignUpRequest req = new ObjectMapper().readValue(loginRequest.getInputStream(), LoginRequest.class);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword(), Collections.emptyList());

        Authentication authentication = authenticationManager.authenticate(authToken);

        /*Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );*/

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);
        JwtAuthenticationResponse res = JwtAuthenticationResponse.builder()
                .token(jwt).userName(authentication.getName())
                .roles(AuthorityUtils.authorityListToSet(authentication.getAuthorities()))
                .build();
        return ResponseEntity.ok(res);
    }
}
