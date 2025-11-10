package com.capstone.ticketing_system.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "users") // Collection name in MongoDB
public class User {
    @Id
    private String id;
    private String name;
    private int role;

    @Indexed(unique = true) // Email as key identifier - should be unique
    private String email;
    private String password;

    // New field for storing avatar image (base64 encoded)
    private String avatarImage;



    // Getters and Setters

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getRole() { return role; }
    public void setRole(int role) { this.role = role; }

    public String getAvatarImage() { return avatarImage; }
    public void setAvatarImage(String avatarImage) { this.avatarImage = avatarImage; }
}