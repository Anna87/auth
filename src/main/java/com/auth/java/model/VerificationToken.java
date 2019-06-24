package com.auth.java.model;

import com.auth.java.constants.enums.TokenVerificationStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Builder(toBuilder = true)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VerificationToken {
    @Id
    private String id;
    private String token;
    private User user;
    private Date expiryDate;
    private TokenVerificationStatus status;
}
