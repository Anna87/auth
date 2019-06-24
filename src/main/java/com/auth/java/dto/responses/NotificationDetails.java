package com.auth.java.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Map;


@Value
@AllArgsConstructor
@Builder(toBuilder = true)
public class NotificationDetails {
    private final Map<String,Object> templateParam;
    private final String recipient;
    private final String subject;
    private final String templateName;
}
