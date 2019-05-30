package com.auth.java.dto;


import lombok.*;

import javax.validation.constraints.NotBlank;

@Builder(toBuilder = true)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
