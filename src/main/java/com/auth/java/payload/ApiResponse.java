package com.auth.java.payload;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ApiResponse {
    private Boolean success;
    private String message;
}