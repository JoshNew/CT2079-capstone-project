package com.capstone.ticketing_system.models.repositories;

import com.capstone.ticketing_system.models.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    // Find all events by organizer ID
    List<Event> findByOrganizerId(String organizerId);

    // Find events by name (for search functionality)
    List<Event> findByNameContainingIgnoreCase(String name);

    // Find events by location
    List<Event> findByLocationContainingIgnoreCase(String location);
}