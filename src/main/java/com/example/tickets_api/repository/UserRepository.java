package com.example.tickets_api.repository;

import com.example.tickets_api.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Data access for {@link AppUser} documents. Inherits standard CRUD from
 * {@link MongoRepository}; the custom finder is derived from its method name.
 */
public interface UserRepository extends MongoRepository<AppUser, String> {

    /**
     * Looks up a user by username.
     *
     * @param username the unique login name
     * @return the user if present, otherwise an empty {@link Optional}
     */
    Optional<AppUser> findByUsername(String username);
}
