package com.auth.java.fiter;

import com.auth.java.config.JwtConfig;
import com.auth.java.payload.SignUpRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter   {

    // We use auth manager to validate the user credentials
    private AuthenticationManager authManager;

    private final JwtConfig jwtConfig;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authManager, JwtConfig jwtConfig) {
        this.authManager = authManager;
        this.jwtConfig = jwtConfig;

        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(jwtConfig.getUri(), "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {

            //UserCredentials creds = new ObjectMapper().readValue(request.getInputStream(), UserCredentials.class);
            SignUpRequest req = new ObjectMapper().readValue(request.getInputStream(), SignUpRequest.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    req.getUsername(), req.getPassword(), Collections.emptyList());

            return authManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // A (temporary) class just to represent the user credentials
    private static class UserCredentials {

        private String username, password, name, email;

        public String getUsername() {
            return username;
        }
        public String getName() {
            return username;
        }
        public String getEmail() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }

    }
}
