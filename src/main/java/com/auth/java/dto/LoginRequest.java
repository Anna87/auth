package com.auth.java.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;

@Builder
@Value
public class LoginRequest {
    @NotBlank
    private final String username;
    @NotBlank
    private final String password;
}
