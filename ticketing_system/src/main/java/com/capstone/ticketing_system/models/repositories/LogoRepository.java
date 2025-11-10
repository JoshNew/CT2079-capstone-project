package com.capstone.ticketing_system.models.repositories;

import com.capstone.ticketing_system.models.Logo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogoRepository extends MongoRepository<Logo, String> {
    // Basic CRUD operations are provided by MongoRepository
}
