package com.auth.java.repositories;


import com.auth.java.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    //Optional<User> findByEmail(String email);

    //Optional<User> findByUsernameOrEmail(String username, String email);

    List<User> findByIdIn(List<Long> userIds);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String email);

    //Boolean exists6yByUsername(String username);

    Boolean existsByEmail(String email);

}

