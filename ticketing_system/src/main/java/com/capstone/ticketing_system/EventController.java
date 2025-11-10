package com.capstone.ticketing_system;

import com.capstone.ticketing_system.models.Event;
import com.capstone.ticketing_system.models.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    // GET all events
    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // GET event by ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable String id) {
        Optional<Event> event = eventRepository.findById(id);
        return event.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET events by organizer ID
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<Event>> getEventsByOrganizerId(@PathVariable String organizerId) {
        List<Event> events = eventRepository.findByOrganizerId(organizerId);
        return ResponseEntity.ok(events);
    }

    // GET events by name (search)
    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEventsByName(@RequestParam String name) {
        List<Event> events = eventRepository.findByNameContainingIgnoreCase(name);
        return ResponseEntity.ok(events);
    }

    // POST create new event
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        try {
            // Validate that organizerId is provided
            if (event.getOrganizerId() == null || event.getOrganizerId().isEmpty()) {
                return ResponseEntity.badRequest().body("Organizer ID is required");
            }

            if (event.getEventDate() != null && !event.getEventDate().isEmpty()) {
                LocalDate eventDate = LocalDate.parse(event.getEventDate());
                LocalTime eventTime = event.getEventTime() != null && !event.getEventTime().isEmpty()
                        ? LocalTime.parse(event.getEventTime())
                        : LocalTime.of(23, 59); // Default to end of day if no time specified

                LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);

                // Use Singapore timezone for comparison
                LocalDateTime nowSingapore = ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime();
                if (eventDateTime.isBefore(nowSingapore)) {
                    return ResponseEntity.badRequest()
                            .body("Cannot create event in the past. Please select a future date and time.");
                }
            }

            // Set timestamps (using Singapore timezone)
            String timestamp = ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
            event.setCreatedAt(timestamp);
            event.setUpdatedAt(timestamp);

            Event savedEvent = eventRepository.save(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating event: " + e.getMessage());
        }
    }

    // PUT update event
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable String id, @RequestBody Event eventDetails) {
        try {
            Optional<Event> optionalEvent = eventRepository.findById(id);

            if (optionalEvent.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Event event = optionalEvent.get();

            // Update fields
            if (eventDetails.getName() != null && !eventDetails.getName().isEmpty()) {
                event.setName(eventDetails.getName());
            }

            if (eventDetails.getLocation() != null && !eventDetails.getLocation().isEmpty()) {
                event.setLocation(eventDetails.getLocation());
            }

            if (eventDetails.getCategory() != null && !eventDetails.getCategory().isEmpty()) {
                event.setCategory(eventDetails.getCategory());
            }

            if (eventDetails.getDescription() != null) {
                event.setDescription(eventDetails.getDescription());
            }

            if (eventDetails.getEventImage() != null) {
                event.setEventImage(eventDetails.getEventImage());
            }

            if (eventDetails.getEventDate() != null && !eventDetails.getEventDate().isEmpty()) {
                event.setEventDate(eventDetails.getEventDate());
            }

            if (eventDetails.getEventTime() != null && !eventDetails.getEventTime().isEmpty()) {
                event.setEventTime(eventDetails.getEventTime());
            }

            if (eventDetails.getVipSeats() != null) {
                event.setVipSeats(eventDetails.getVipSeats());
            }

            if (eventDetails.getStandardSeats() != null) {
                event.setStandardSeats(eventDetails.getStandardSeats());
            }

            if (eventDetails.getConcessionSeats() != null) {
                event.setConcessionSeats(eventDetails.getConcessionSeats());
            }

            // Update timestamp (using Singapore timezone)
            event.setUpdatedAt(ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME));

            Event updatedEvent = eventRepository.save(event);
            return ResponseEntity.ok(updatedEvent);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE event
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        try {
            if (!eventRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            eventRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}