package com.auth.java.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtConfig {
    @Value("${jwt.config.uri:}")
    private String Uri;

    @Value("${jwt.config.header:}")
    private String header;

    @Value("${jwt.config.prefix:}")
    private String prefix;

    @Value("${jwt.config.expiration}")
    private int expiration;

    @Value("${jwt.config.secret:}")
    private String secret;
}
