package com.capstone.ticketing_system.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "bookings")
public class Booking {
    @Id
    private String id;
    private String eventId;
    private String userId;
    private String userName;
    private String userEmail;
    private List<SeatBooking> seats;
    private double totalPrice;
    private String bookingDate;
    private String status; // "confirmed", "cancelled", "used"
    private String paymentMethod;

    // Inner class for seat booking details
    public static class SeatBooking {
        private String seatType; // "VIP", "Standard", "Concession"
        private int seatNumber;
        private double price;

        public SeatBooking() {}

        public SeatBooking(String seatType, int seatNumber, double price) {
            this.seatType = seatType;
            this.seatNumber = seatNumber;
            this.price = price;
        }

        public String getSeatType() { return seatType; }
        public void setSeatType(String seatType) { this.seatType = seatType; }

        public int getSeatNumber() { return seatNumber; }
        public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }

    // Constructors
    public Booking() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public List<SeatBooking> getSeats() { return seats; }
    public void setSeats(List<SeatBooking> seats) { this.seats = seats; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}