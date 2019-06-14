package com.auth.java.dto.requests;


import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Builder
@Value
public class ActivateRequest {
    @NotBlank
    private String password;
    @NotBlank
    private String token;
}
