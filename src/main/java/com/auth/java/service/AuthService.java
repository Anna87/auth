package com.auth.java.service;

import com.auth.java.dto.JwtAuthenticationResponse;
import com.auth.java.dto.LoginRequest;
import com.auth.java.dto.RegisterRequest;
import com.auth.java.enums.TokenVerificationStatus;
import com.auth.java.exceptions.EmailExistException;
import com.auth.java.exceptions.NotFoundException;
import com.auth.java.exceptions.UserRegistrationExpiredException;
import com.auth.java.enums.RoleName;
import com.auth.java.model.User;
import com.auth.java.model.VerificationToken;
import com.auth.java.repositories.UserRepository;
import com.auth.java.repositories.VerificationTokenRepository;
import com.auth.java.security.JwtTokenProvider;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.*;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final int expirationValue = 60 * 24;

    private final JwtTokenProvider tokenProvider;

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final VerificationTokenRepository tokenRepository;

    private final JmsTemplate jmsTemplate;

    public JwtAuthenticationResponse authenticateUser(@Valid final LoginRequest loginRequest) {
        final UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword(),
                Collections.emptyList());

        final Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String jwt = tokenProvider.createToken(authentication);

        return JwtAuthenticationResponse.builder()
                .token(jwt).userName(authentication.getName())
                .roles(AuthorityUtils.authorityListToSet(authentication.getAuthorities()))
                .build();
    }

    public void registerUser(final RegisterRequest registerRequest) {

        Optional<User> userOptional = userRepository.findByEmail(registerRequest.getEmail());
        if (userOptional.isPresent()) {
            throw new EmailExistException();
        }

        final User newUser = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(null)
                .roles(Sets.newHashSet(RoleName.ROLE_USER.name()))
                .enabled(false)
                .build();
        final User user = userRepository.save(newUser);

        final String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token).user(user)
                .expiryDate(calculateExpiryDate(expirationValue))
                .status(TokenVerificationStatus.ACTIVE)
                .build();
        tokenRepository.save(verificationToken);

        jmsTemplate.convertAndSend("verificationToken-queue", verificationToken);

    }

    public Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public JwtAuthenticationResponse activateUser(final String token, final String password) {
        final VerificationToken verificationToken = tokenRepository.findByTokenAndStatus(token, TokenVerificationStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Token not found"));

        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate()
                .getTime()
                - cal.getTime()
                .getTime()) <= 0) {
            throw new UserRegistrationExpiredException();
        }

        final User user = verificationToken.getUser();
        final User userToUpdate = user.toBuilder()
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .build();
        userRepository.save(userToUpdate);

        final VerificationToken verificationTokenToUpdate = verificationToken.toBuilder()
                .status(TokenVerificationStatus.OBSOLETE)
                .build();
        tokenRepository.save(verificationTokenToUpdate);

        final LoginRequest loginRequest = LoginRequest.builder()
                .username(user.getUsername())
                .password(password)
                .build();

        return authenticateUser(loginRequest);
    }


    public boolean validateToken(String token) {
        return tokenProvider.validateToken(token);
    }
}
