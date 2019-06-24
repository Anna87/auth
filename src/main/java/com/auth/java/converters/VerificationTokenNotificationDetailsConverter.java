package com.auth.java.converters;

import com.auth.java.dto.responses.NotificationDetails;
import com.auth.java.model.VerificationToken;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class VerificationTokenNotificationDetailsConverter implements GenericConverter<NotificationDetails,VerificationToken> {
    @Override
    public NotificationDetails convert(final VerificationToken token) {
        final Map<String,Object> templateParam = new HashMap<>();
        templateParam.put("verificationToken", token.getToken());
        templateParam.put("username", token.getUser().getUsername());

        return NotificationDetails.builder()
                .recipient(token.getUser().getEmail())
                .subject("Confirm registration")
                .templateParam(templateParam)
                .templateName("verificationTokenNotification.ftl")
                .build();
    }
}
