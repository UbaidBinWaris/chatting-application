package com.example.backend.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AdminPasswordHashTest {

    @Test
    public void generateAdminPasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String plainPassword = "admin123";
        String hashedPassword = encoder.encode(plainPassword);

        System.out.println("=".repeat(80));
        System.out.println("ADMIN PASSWORD HASH");
        System.out.println("=".repeat(80));
        System.out.println("Email: admin@chatme.com");
        System.out.println("Plain Password: " + plainPassword);
        System.out.println("BCrypt Hash: " + hashedPassword);
        System.out.println("=".repeat(80));
        System.out.println("\nSQL Insert Statement:");
        System.out.println("DELETE FROM users WHERE email = 'admin@chatme.com';");
        System.out.println("INSERT INTO users (email, password, role, privilege_level)");
        System.out.println("VALUES ('admin@chatme.com', '" + hashedPassword + "', 'ADMIN', 99);");
        System.out.println("=".repeat(80));

        // Verify the hash works
        boolean matches = encoder.matches(plainPassword, hashedPassword);
        System.out.println("\nPassword verification: " + (matches ? "SUCCESS" : "FAILED"));
        System.out.println("=".repeat(80));
    }
}
