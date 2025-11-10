package com.capstone.ticketing_system.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "advertisements")
public class Advertisement {

    @Id
    private String id;

    private String name;           // File name
    private String imageData;      // Base64 encoded image data
    private String type;           // MIME type (image/jpeg, image/png, image/gif)
    private String size;           // File size (e.g., "2.5 MB")
    private int displayOrder;      // Order in which to display (0, 1, 2...)
    private boolean active;        // Whether ad is active
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Advertisement() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Advertisement(String name, String imageData, String type, String size, int displayOrder) {
        this.name = name;
        this.imageData = imageData;
        this.type = type;
        this.size = size;
        this.displayOrder = displayOrder;
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}