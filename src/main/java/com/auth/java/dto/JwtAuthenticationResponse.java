package com.auth.java.dto;


import lombok.*;

import java.util.Set;

@Builder(toBuilder = true)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthenticationResponse {
    private String token;
    private String userName;
    private Set<String> roles;
}
