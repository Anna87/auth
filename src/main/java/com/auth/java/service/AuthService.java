package com.auth.java.service;

import com.auth.java.model.RoleName;
import com.auth.java.model.User;
import com.auth.java.payload.JwtAuthenticationResponse;
import com.auth.java.payload.LoginRequest;
import com.auth.java.payload.SignUpRequest;
import com.auth.java.repositories.UserRepository;
import com.auth.java.security.JwtTokenProvider;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword(), Collections.emptyList());

        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        return JwtAuthenticationResponse.builder()
                .token(jwt).userName(authentication.getName())
                .roles(AuthorityUtils.authorityListToSet(authentication.getAuthorities()))
                .build();
    }

    public void registerUser(SignUpRequest signUpRequest){

        User newUser = User.builder()
                .email(signUpRequest.getEmail())
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .roles(Sets.newHashSet(RoleName.ROLE_USER.name()))
                .build();

        userRepository.save(newUser);
    }
}
