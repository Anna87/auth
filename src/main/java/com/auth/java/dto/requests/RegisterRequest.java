package com.auth.java.dto.requests;


import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Value
public class RegisterRequest {
    @NotBlank
    private final String username;
    @Email
    private final String email;

}
