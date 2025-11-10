package com.capstone.ticketing_system;

import com.capstone.ticketing_system.models.User;
import com.capstone.ticketing_system.models.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // GET all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // GET user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET user by email (key identifier)
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        List<User> users = userRepository.findByEmail(email);
        if (!users.isEmpty()) {
            return ResponseEntity.ok(users.getFirst()); // Return first user found
        }
        return ResponseEntity.notFound().build();
    }

    // POST create new user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            // Check if email already exists
            List<User> existingUsers = userRepository.findByEmail(user.getEmail());
            if (!existingUsers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Email already exists
            }

            // Hash password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            User savedUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT update user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userDetails){
        try {
            Optional<User> optionalUser = userRepository.findById(id);

            if (optionalUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = optionalUser.get();

            // Check if email is being changed and if new email already exists
            if (!user.getEmail().equals(userDetails.getEmail())) {
                List<User> existingUsers = userRepository.findByEmail(userDetails.getEmail());
                if (!existingUsers.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Email already exists
                }
            }

            // Update name if provided
            if (userDetails.getName() != null && !userDetails.getName().isEmpty()) {
                user.setName(userDetails.getName());
            }

            // Update email if provided
            if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
                user.setEmail(userDetails.getEmail());
            }

            // Update role if provided
            if (userDetails.getRole() > 0) {
                user.setRole(userDetails.getRole());
            }

            // Only update password if provided (not null/empty)
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                // Hash password before saving
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            // Update avatar image if provided
            if (userDetails.getAvatarImage() != null) {
                user.setAvatarImage(userDetails.getAvatarImage());
            }

            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET users by name
    @GetMapping("/search")
    public List<User> getUsersByName(@RequestParam String name) {
        return userRepository.findByName(name);
    }

    // POST login endpoint
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
        List<User> users = userRepository.findByEmail(loginRequest.getEmail());

        if (!users.isEmpty()) {
            User user = users.getFirst(); // Get first user (should be only one due to unique constraint)
            // Use password encoder to verify password
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.ok(user);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // POST change password endpoint (for authenticated users)
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            // Find user by ID
            Optional<User> optionalUser = userRepository.findById(request.getUserId());

            if (optionalUser.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = optionalUser.get();

            // Verify current password
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Update to new password (hashed)
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST reset password endpoint (validates email and updates password)
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            // Find user by email
            List<User> users = userRepository.findByEmail(request.getEmail());

            if (users.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = users.getFirst();

            // Update password (hashed)
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Inner class for login request
    public static class LoginRequest {
        private String email;
        private String password;

        public LoginRequest() {}

        public String getEmail() { return email; }

        public String getPassword() { return password; }
    }

    // Inner class for change password request
    public static class ChangePasswordRequest {
        private String userId;
        private String currentPassword;
        private String newPassword;

        public ChangePasswordRequest() {}

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    // Inner class for reset password request
    public static class ResetPasswordRequest {
        private String email;
        private String newPassword;

        public ResetPasswordRequest() {}

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}