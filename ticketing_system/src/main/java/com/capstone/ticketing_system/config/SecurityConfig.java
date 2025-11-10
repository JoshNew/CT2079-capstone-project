package com.capstone.ticketing_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    /**
     * Creates a BCryptPasswordEncoder bean for password hashing.
     * BCrypt is a strong, adaptive hashing function designed for password security.
     * It automatically handles salt generation and includes a work factor to remain
     * resistant to brute-force attacks as computing power increases.
     *
     * @return PasswordEncoder instance using BCrypt algorithm
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
