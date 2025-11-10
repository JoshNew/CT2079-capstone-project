package com.capstone.ticketing_system.models.repositories;

import com.capstone.ticketing_system.models.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {
    // Find all bookings for a specific event
    List<Booking> findByEventId(String eventId);

    // Find all bookings for a specific user
    List<Booking> findByUserId(String userId);

    // Find bookings by event and status
    List<Booking> findByEventIdAndStatus(String eventId, String status);
}