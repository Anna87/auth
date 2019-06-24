package com.auth.java.security;

import com.auth.java.config.JwtConfig;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.stream.Collectors;

import static com.auth.java.constants.Constants.AUTHORITIES;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

    public String createToken(final Authentication auth){
        final Long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(auth.getName())
                .claim(AUTHORITIES, auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExpiration() * 1000))  // in milliseconds
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
                .compact();
    }

    public Boolean validateToken(final String authToken){ // void throw custom exception, on frontend check status
        try {
            final String token = authToken.substring(jwtConfig.getPrefix().length());
            Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret().getBytes())
                    .parseClaimsJws(token);
            return true;
        } catch (RuntimeException ex) { //TODO
            log.error("Invalid JWT token", ex);
        }
        return false;
    }

}
