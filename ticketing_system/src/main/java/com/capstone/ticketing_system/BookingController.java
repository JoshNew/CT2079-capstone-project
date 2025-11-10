package com.capstone.ticketing_system;

import com.capstone.ticketing_system.models.Booking;
import com.capstone.ticketing_system.models.Event;
import com.capstone.ticketing_system.models.repositories.BookingRepository;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.Map;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EventRepository eventRepository;

    // GET all bookings
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // GET booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable String id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET bookings by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUserId(@PathVariable String userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    // GET bookings by event ID
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Booking>> getBookingsByEventId(@PathVariable String eventId) {
        List<Booking> bookings = bookingRepository.findByEventId(eventId);
        return ResponseEntity.ok(bookings);
    }

    // GET booked seats for an event (returns seat numbers by type)
    @GetMapping("/event/{eventId}/booked-seats")
    public ResponseEntity<Map<String, List<Integer>>> getBookedSeats(@PathVariable String eventId) {
        // Get both confirmed and used bookings (not cancelled)
        List<Booking> confirmedBookings = bookingRepository.findByEventIdAndStatus(eventId, "confirmed");
        List<Booking> usedBookings = bookingRepository.findByEventIdAndStatus(eventId, "used");

        List<Booking> bookings = new ArrayList<>();
        bookings.addAll(confirmedBookings);
        bookings.addAll(usedBookings);

        Map<String, List<Integer>> bookedSeats = new HashMap<>();
        bookedSeats.put("VIP", new ArrayList<>());
        bookedSeats.put("Standard", new ArrayList<>());
        bookedSeats.put("Concession", new ArrayList<>());

        for (Booking booking : bookings) {
            for (Booking.SeatBooking seat : booking.getSeats()) {
                List<Integer> seatList = bookedSeats.get(seat.getSeatType());
                if (seatList != null) {
                    seatList.add(seat.getSeatNumber());
                }
            }
        }

        return ResponseEntity.ok(bookedSeats);
    }

    // POST create new booking
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        try {
            // Validate event exists
            Optional<Event> eventOpt = eventRepository.findById(booking.getEventId());
            if (eventOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Event not found");
            }

            Event event = eventOpt.get();

            // Check if user already has bookings for this event
            List<Booking> userEventBookings = bookingRepository.findByEventId(booking.getEventId())
                    .stream()
                    .filter(b -> b.getUserId().equals(booking.getUserId()))
                    .filter(b -> !b.getStatus().equals("cancelled")) // Only count non-cancelled bookings
                    .collect(Collectors.toList());

            // Count existing tickets for this user and event
            int existingTicketCount = 0;
            for (Booking existingBooking : userEventBookings) {
                existingTicketCount += existingBooking.getSeats().size();
            }

            // Count tickets in current booking request
            int requestedTicketCount = booking.getSeats().size();
            int totalTickets = existingTicketCount + requestedTicketCount;

            // Check if total exceeds 10
            if (totalTickets > 10) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Cannot book more than 10 tickets per event. You already have "
                                + existingTicketCount + " ticket(s) booked for this event.");
            }

            LocalDate eventDate = LocalDate.parse(event.getEventDate());
            LocalTime eventTime = event.getEventTime() != null
                    ? LocalTime.parse(event.getEventTime())
                    : LocalTime.of(23, 59); // Default to end of day if no time

            LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);

            // Use Singapore timezone for comparison
            LocalDateTime nowSingapore = ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime();
            if (eventDateTime.isBefore(nowSingapore)) {
                return ResponseEntity.badRequest()
                        .body("Cannot book tickets for past events");
            }

            // Check if seats are still available
            List<Booking> existingBookings = bookingRepository.findByEventIdAndStatus(booking.getEventId(), "confirmed");
            Map<String, List<Integer>> bookedSeats = getBookedSeatsMap(existingBookings);

            // Validate requested seats are not already booked
            for (Booking.SeatBooking seat : booking.getSeats()) {
                List<Integer> alreadyBooked = bookedSeats.get(seat.getSeatType());
                if (alreadyBooked != null && alreadyBooked.contains(seat.getSeatNumber())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("Seat " + seat.getSeatType() + "-" + seat.getSeatNumber() + " is already booked");
                }
            }

            // Update event seat availability
            Map<String, Integer> seatCounts = countSeatsByType(booking.getSeats());

            if (event.getVipSeats() != null && seatCounts.containsKey("VIP")) {
                int newAvailable = event.getVipSeats().getAvailableSeats() - seatCounts.get("VIP");
                if (newAvailable < 0) {
                    return ResponseEntity.badRequest().body("Not enough VIP seats available");
                }
                event.getVipSeats().setAvailableSeats(newAvailable);
            }

            if (event.getStandardSeats() != null && seatCounts.containsKey("Standard")) {
                int newAvailable = event.getStandardSeats().getAvailableSeats() - seatCounts.get("Standard");
                if (newAvailable < 0) {
                    return ResponseEntity.badRequest().body("Not enough Standard seats available");
                }
                event.getStandardSeats().setAvailableSeats(newAvailable);
            }

            if (event.getConcessionSeats() != null && seatCounts.containsKey("Concession")) {
                int newAvailable = event.getConcessionSeats().getAvailableSeats() - seatCounts.get("Concession");
                if (newAvailable < 0) {
                    return ResponseEntity.badRequest().body("Not enough Concession seats available");
                }
                event.getConcessionSeats().setAvailableSeats(newAvailable);
            }

            // Set booking metadata (using Singapore timezone)
            booking.setBookingDate(ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME));
            booking.setStatus("confirmed");

            // Save booking
            Booking savedBooking = bookingRepository.save(booking);

            // Update event (using Singapore timezone)
            event.setUpdatedAt(ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME));
            eventRepository.save(event);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating booking: " + e.getMessage());
        }
    }

    // Helper method to get booked seats map
    private Map<String, List<Integer>> getBookedSeatsMap(List<Booking> bookings) {
        Map<String, List<Integer>> bookedSeats = new HashMap<>();
        bookedSeats.put("VIP", new ArrayList<>());
        bookedSeats.put("Standard", new ArrayList<>());
        bookedSeats.put("Concession", new ArrayList<>());

        for (Booking booking : bookings) {
            for (Booking.SeatBooking seat : booking.getSeats()) {
                List<Integer> seatList = bookedSeats.get(seat.getSeatType());
                if (seatList != null) {
                    seatList.add(seat.getSeatNumber());
                }
            }
        }

        return bookedSeats;
    }

    // Helper method to count seats by type
    private Map<String, Integer> countSeatsByType(List<Booking.SeatBooking> seats) {
        return seats.stream()
                .collect(Collectors.groupingBy(
                        Booking.SeatBooking::getSeatType,
                        Collectors.summingInt(seat -> 1)
                ));
    }

    // PUT cancel booking (update status to cancelled instead of deleting)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable String id) {
        try {
            Optional<Booking> bookingOpt = bookingRepository.findById(id);
            if (bookingOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Booking booking = bookingOpt.get();

            // Check if already cancelled
            if ("cancelled".equals(booking.getStatus())) {
                return ResponseEntity.badRequest().body("Booking is already cancelled");
            }

            // Update event seat availability
            Optional<Event> eventOpt = eventRepository.findById(booking.getEventId());
            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();
                Map<String, Integer> seatCounts = countSeatsByType(booking.getSeats());

                if (event.getVipSeats() != null && seatCounts.containsKey("VIP")) {
                    event.getVipSeats().setAvailableSeats(
                            event.getVipSeats().getAvailableSeats() + seatCounts.get("VIP")
                    );
                }

                if (event.getStandardSeats() != null && seatCounts.containsKey("Standard")) {
                    event.getStandardSeats().setAvailableSeats(
                            event.getStandardSeats().getAvailableSeats() + seatCounts.get("Standard")
                    );
                }

                if (event.getConcessionSeats() != null && seatCounts.containsKey("Concession")) {
                    event.getConcessionSeats().setAvailableSeats(
                            event.getConcessionSeats().getAvailableSeats() + seatCounts.get("Concession")
                    );
                }

                // Update event timestamp (using Singapore timezone)
                event.setUpdatedAt(ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime().format(DateTimeFormatter.ISO_DATE_TIME));
                eventRepository.save(event);
            }

            // Update booking status to cancelled
            booking.setStatus("cancelled");
            Booking updatedBooking = bookingRepository.save(booking);

            return ResponseEntity.ok(updatedBooking);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error cancelling booking: " + e.getMessage());
        }
    }

    // PUT mark booking as used (for ticket verification)
    @PutMapping("/{id}/use")
    public ResponseEntity<?> markBookingAsUsed(@PathVariable String id) {
        try {
            Optional<Booking> bookingOpt = bookingRepository.findById(id);
            if (bookingOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Booking booking = bookingOpt.get();

            // Check current status
            if ("cancelled".equals(booking.getStatus())) {
                return ResponseEntity.badRequest().body("Cannot use a cancelled booking");
            }

            if ("used".equals(booking.getStatus())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Booking already used");
            }

            // Update booking status to used
            booking.setStatus("used");
            Booking updatedBooking = bookingRepository.save(booking);

            return ResponseEntity.ok(updatedBooking);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error marking booking as used: " + e.getMessage());
        }
    }
    // PUT update booking status (for auto-updating to "passed")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable String id, @RequestBody Map<String, String> statusUpdate) {
        try {
            Optional<Booking> bookingOpt = bookingRepository.findById(id);
            if (bookingOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Booking booking = bookingOpt.get();
            String newStatus = statusUpdate.get("status");

            // Validate status
            if (newStatus == null || (!newStatus.equals("passed") && !newStatus.equals("confirmed")
                    && !newStatus.equals("used") && !newStatus.equals("cancelled"))) {
                return ResponseEntity.badRequest().body("Invalid status value");
            }

            // Update booking status
            booking.setStatus(newStatus);
            Booking updatedBooking = bookingRepository.save(booking);

            return ResponseEntity.ok(updatedBooking);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating booking status: " + e.getMessage());
        }
    }
}