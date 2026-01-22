package com.example.backend.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Password hashing utility using BCrypt
 * BCrypt is a secure one-way hashing algorithm designed for password storage
 */
@Component
public class PasswordHashUtil {

    private final PasswordEncoder passwordEncoder;

    public PasswordHashUtil() {
        // BCrypt with strength 12 (2^12 iterations)
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    /**
     * Hash a plaintext password
     *
     * @param plainPassword The plaintext password
     * @return BCrypt hashed password
     */
    public String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * Verify a plaintext password against a hashed password
     *
     * @param plainPassword The plaintext password to verify
     * @param hashedPassword The stored BCrypt hash
     * @return true if password matches, false otherwise
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}

