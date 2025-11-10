package com.capstone.ticketing_system.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "events")
public class Event {
    @Id
    private String id;
    private String name;
    private String location;
    private String category;
    private String description;
    private String eventImage; // Base64 encoded image
    private String organizerId; // Reference to the User who created it
    private String eventDate; // Date of the event (YYYY-MM-DD)
    private String eventTime; // Time of the event (HH:MM)

    // Seat types with prices
    private SeatInfo vipSeats;
    private SeatInfo standardSeats;
    private SeatInfo concessionSeats;

    private String createdAt;
    private String updatedAt;

    // Inner class for seat information
    public static class SeatInfo {
        private int totalSeats;
        private int availableSeats;
        private double price;

        public SeatInfo() {}

        public SeatInfo(int totalSeats, int availableSeats, double price) {
            this.totalSeats = totalSeats;
            this.availableSeats = availableSeats;
            this.price = price;
        }

        public int getTotalSeats() { return totalSeats; }
        public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

        public int getAvailableSeats() { return availableSeats; }
        public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }

    // Constructors
    public Event() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEventImage() { return eventImage; }
    public void setEventImage(String eventImage) { this.eventImage = eventImage; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }

    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }

    public SeatInfo getVipSeats() { return vipSeats; }
    public void setVipSeats(SeatInfo vipSeats) { this.vipSeats = vipSeats; }

    public SeatInfo getStandardSeats() { return standardSeats; }
    public void setStandardSeats(SeatInfo standardSeats) { this.standardSeats = standardSeats; }

    public SeatInfo getConcessionSeats() { return concessionSeats; }
    public void setConcessionSeats(SeatInfo concessionSeats) { this.concessionSeats = concessionSeats; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}