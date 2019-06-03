package com.auth.java.service;

import com.auth.java.dto.JwtAuthenticationResponse;
import com.auth.java.dto.LoginRequest;
import com.auth.java.dto.RegisterRequest;
import com.auth.java.exceptions.EmailExistException;
import com.auth.java.exceptions.NotFoundException;
import com.auth.java.exceptions.UserRegistrationExpiredException;
import com.auth.java.model.RoleName;
import com.auth.java.model.User;
import com.auth.java.model.VerificationToken;
import com.auth.java.repositories.UserRepository;
import com.auth.java.repositories.VerificationTokenRepository;
import com.auth.java.security.JwtTokenProvider;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    private static final int EXPIRATION = 60 * 24;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    VerificationTokenRepository tokenRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest) {
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

    public void registerUser(RegisterRequest registerRequest) {

        Optional<User> userOptional = userRepository.findByEmail(registerRequest.getEmail());
        if (userOptional.isPresent()) {
            throw new EmailExistException();
        }

        User newUser = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(null)
                .roles(Sets.newHashSet(RoleName.ROLE_USER.name()))
                .enabled(false)
                .build();
        User user = userRepository.save(newUser);

        final String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token).user(user).expiryDate(calculateExpiryDate(EXPIRATION))
                .status(TokenVerivicationStatus.ACTIVE)
                .build();
        tokenRepository.save(verificationToken);

        jmsTemplate.convertAndSend("VerificationTokenQueue", verificationToken);

    }

    public Date calculateExpiryDate(int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public JwtAuthenticationResponse activateUser(String token, String password) {
        final VerificationToken verificationToken = tokenRepository.findByTokenAndStatus(token, TokenVerivicationStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Token not found"));

        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate()
                .getTime()
                - cal.getTime()
                .getTime()) <= 0) {
            throw new UserRegistrationExpiredException();
        }

        final User user = verificationToken.getUser();

        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        userRepository.save(user);

        verificationToken.setStatus(TokenVerivicationStatus.OBSOLETE);
        tokenRepository.save(verificationToken);

        LoginRequest loginRequest = LoginRequest.builder().username(user.getUsername()).password(password).build();

        return authenticateUser(loginRequest);
    }


    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }
}
