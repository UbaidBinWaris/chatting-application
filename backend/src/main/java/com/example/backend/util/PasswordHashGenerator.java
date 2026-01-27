package com.example.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes
 * Run this class to generate a hash for the admin password
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String plainPassword = "admin123";
        String hashedPassword = encoder.encode(plainPassword);

        System.out.println("=".repeat(60));
        System.out.println("BCrypt Password Hash Generator");
        System.out.println("=".repeat(60));
        System.out.println("Plain Password: " + plainPassword);
        System.out.println("Hashed Password: " + hashedPassword);
        System.out.println("=".repeat(60));
        System.out.println("\nSQL Insert Statement:");
        System.out.println("INSERT INTO users (email, password, role, privilege_level)");
        System.out.println("VALUES ('admin@chatme.com', '" + hashedPassword + "', 'ADMIN', 99);");
        System.out.println("=".repeat(60));
    }
}
