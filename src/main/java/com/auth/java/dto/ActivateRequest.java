package com.auth.java.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class ActivateRequest {
    @NotBlank
    private String password;
    @NotBlank
    private String token;
}
