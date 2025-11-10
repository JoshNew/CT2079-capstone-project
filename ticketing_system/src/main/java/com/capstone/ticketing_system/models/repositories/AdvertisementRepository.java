package com.capstone.ticketing_system.models.repositories;

import com.capstone.ticketing_system.models.Advertisement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends MongoRepository<Advertisement, String> {

    // Find all active advertisements ordered by displayOrder
    List<Advertisement> findByActiveOrderByDisplayOrderAsc(boolean active);

    // Find all advertisements ordered by displayOrder
    List<Advertisement> findAllByOrderByDisplayOrderAsc();
}