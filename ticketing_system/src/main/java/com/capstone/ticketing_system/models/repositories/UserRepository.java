package com.capstone.ticketing_system.models.repositories;

import com.capstone.ticketing_system.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    // Custom query methods
    List<User> findByName(String name);

    // Email queries - since email is the key identifier
    List<User> findByEmail(String email);
    Optional<User> findFirstByEmail(String email); // Alternative method for unique email lookup

    // Check if email exists (useful for validation)
    boolean existsByEmail(String email);
}