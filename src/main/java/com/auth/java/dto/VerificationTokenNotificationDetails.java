package com.auth.java.dto;

import lombok.Builder;
import lombok.Value;

@Builder(toBuilder = true)
@Value
public class VerificationTokenNotificationDetails {
    private final String verificationToken;
    private final String username;
    private final String userEmail;
}
