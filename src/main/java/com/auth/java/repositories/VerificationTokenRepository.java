package com.auth.java.repositories;


import com.auth.java.model.VerificationToken;
import com.auth.java.service.TokenVerivicationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {
    Optional<VerificationToken> findByTokenAndStatus(String token, TokenVerivicationStatus status);
}
