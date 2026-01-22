package com.example.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for PasswordHashUtil - BCrypt password hashing
 */
@DisplayName("PasswordHashUtil Tests")
class PasswordHashUtilTest {

    private PasswordHashUtil passwordHashUtil;

    @BeforeEach
    void setUp() {
        passwordHashUtil = new PasswordHashUtil();
    }

    @Test
    @DisplayName("Should hash password successfully")
    void testHashPassword() {
        // Given
        String plainPassword = "MySecurePassword123!";

        // When
        String hashedPassword = passwordHashUtil.hashPassword(plainPassword);

        // Then
        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$"));
    }

    @Test
    @DisplayName("Should produce different hashes for same password (salt)")
    void testHashPasswordWithSalt() {
        // Given
        String plainPassword = "password123";

        // When
        String hash1 = passwordHashUtil.hashPassword(plainPassword);
        String hash2 = passwordHashUtil.hashPassword(plainPassword);

        // Then
        assertNotEquals(hash1, hash2, "Each hash should be unique due to random salt");
    }

    @Test
    @DisplayName("Should verify correct password")
    void testVerifyPasswordSuccess() {
        // Given
        String plainPassword = "MySecurePassword123!";
        String hashedPassword = passwordHashUtil.hashPassword(plainPassword);

        // When
        boolean result = passwordHashUtil.verifyPassword(plainPassword, hashedPassword);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should reject incorrect password")
    void testVerifyPasswordFailure() {
        // Given
        String plainPassword = "MySecurePassword123!";
        String wrongPassword = "WrongPassword456!";
        String hashedPassword = passwordHashUtil.hashPassword(plainPassword);

        // When
        boolean result = passwordHashUtil.verifyPassword(wrongPassword, hashedPassword);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should throw exception for null password hashing")
    void testHashPasswordNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            passwordHashUtil.hashPassword(null);
        });
    }

    @Test
    @DisplayName("Should throw exception for empty password hashing")
    void testHashPasswordEmpty() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            passwordHashUtil.hashPassword("");
        });
    }

    @Test
    @DisplayName("Should return false for null password verification")
    void testVerifyPasswordNullPassword() {
        // Given
        String hashedPassword = passwordHashUtil.hashPassword("password123");

        // When
        boolean result = passwordHashUtil.verifyPassword(null, hashedPassword);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false for null hash verification")
    void testVerifyPasswordNullHash() {
        // When
        boolean result = passwordHashUtil.verifyPassword("password123", null);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle special characters in password")
    void testHashPasswordSpecialCharacters() {
        // Given
        String plainPassword = "P@ssw0rd!#$%^&*()_+-=[]{}|;:',.<>?/~`";

        // When
        String hashedPassword = passwordHashUtil.hashPassword(plainPassword);
        boolean verified = passwordHashUtil.verifyPassword(plainPassword, hashedPassword);

        // Then
        assertNotNull(hashedPassword);
        assertTrue(verified);
    }

    @Test
    @DisplayName("Should handle long passwords")
    void testHashPasswordLongPassword() {
        // Given
        String longPassword = "a".repeat(100);

        // When
        String hashedPassword = passwordHashUtil.hashPassword(longPassword);
        boolean verified = passwordHashUtil.verifyPassword(longPassword, hashedPassword);

        // Then
        assertNotNull(hashedPassword);
        assertTrue(verified);
    }

    @Test
    @DisplayName("Should be case sensitive")
    void testPasswordCaseSensitive() {
        // Given
        String password = "Password123";
        String hashedPassword = passwordHashUtil.hashPassword(password);

        // When
        boolean correctCase = passwordHashUtil.verifyPassword("Password123", hashedPassword);
        boolean wrongCase = passwordHashUtil.verifyPassword("password123", hashedPassword);

        // Then
        assertTrue(correctCase);
        assertFalse(wrongCase);
    }
}
