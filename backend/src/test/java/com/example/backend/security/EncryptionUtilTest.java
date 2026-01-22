package com.example.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for EncryptionUtil - AES-256-GCM encryption
 */
@DisplayName("EncryptionUtil Tests")
class EncryptionUtilTest {

    private EncryptionUtil encryptionUtil;

    @BeforeEach
    void setUp() {
        encryptionUtil = new EncryptionUtil();
    }

    @Test
    @DisplayName("Should encrypt and decrypt text successfully")
    void testEncryptDecrypt() {
        // Given
        String plaintext = "test@example.com";

        // When
        String encrypted = encryptionUtil.encrypt(plaintext);
        String decrypted = encryptionUtil.decrypt(encrypted);

        // Then
        assertNotNull(encrypted);
        assertNotEquals(plaintext, encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    @DisplayName("Should produce different ciphertext for same plaintext (random IV)")
    void testRandomIV() {
        // Given
        String plaintext = "sensitive_data";

        // When
        String encrypted1 = encryptionUtil.encrypt(plaintext);
        String encrypted2 = encryptionUtil.encrypt(plaintext);

        // Then
        assertNotEquals(encrypted1, encrypted2, "Each encryption should produce different ciphertext due to random IV");
        assertEquals(plaintext, encryptionUtil.decrypt(encrypted1));
        assertEquals(plaintext, encryptionUtil.decrypt(encrypted2));
    }

    @Test
    @DisplayName("Should handle null input for encryption")
    void testEncryptNull() {
        // When
        String result = encryptionUtil.encrypt(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle empty string encryption")
    void testEncryptEmptyString() {
        // When
        String result = encryptionUtil.encrypt("");

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null input for decryption")
    void testDecryptNull() {
        // When
        String result = encryptionUtil.decrypt(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should encrypt special characters correctly")
    void testEncryptSpecialCharacters() {
        // Given
        String plaintext = "user@domain.com!#$%^&*()";

        // When
        String encrypted = encryptionUtil.encrypt(plaintext);
        String decrypted = encryptionUtil.decrypt(encrypted);

        // Then
        assertEquals(plaintext, decrypted);
    }

    @Test
    @DisplayName("Should encrypt Unicode characters correctly")
    void testEncryptUnicode() {
        // Given
        String plaintext = "用户名@example.com";

        // When
        String encrypted = encryptionUtil.encrypt(plaintext);
        String decrypted = encryptionUtil.decrypt(encrypted);

        // Then
        assertEquals(plaintext, decrypted);
    }

    @Test
    @DisplayName("Should encrypt long text correctly")
    void testEncryptLongText() {
        // Given
        String plaintext = "a".repeat(1000);

        // When
        String encrypted = encryptionUtil.encrypt(plaintext);
        String decrypted = encryptionUtil.decrypt(encrypted);

        // Then
        assertEquals(plaintext, decrypted);
    }

    @Test
    @DisplayName("Should throw exception for invalid ciphertext")
    void testDecryptInvalidCiphertext() {
        // Given
        String invalidCiphertext = "invalid_base64_data";

        // Then
        assertThrows(RuntimeException.class, () -> {
            encryptionUtil.decrypt(invalidCiphertext);
        });
    }

    @Test
    @DisplayName("Should return encryption key in Base64 format")
    void testGetKeyAsBase64() {
        // When
        String key = encryptionUtil.getKeyAsBase64();

        // Then
        assertNotNull(key);
        assertTrue(key.length() > 0);
        // Base64 encoded 256-bit key should be 44 characters
        assertEquals(44, key.length());
    }

    @Test
    @DisplayName("Should be able to use custom encryption key")
    void testCustomKey() {
        // Given
        String originalKey = encryptionUtil.getKeyAsBase64();
        EncryptionUtil util1 = new EncryptionUtil(originalKey);
        String plaintext = "test_data";

        // When
        String encrypted = util1.encrypt(plaintext);
        EncryptionUtil util2 = new EncryptionUtil(originalKey);
        String decrypted = util2.decrypt(encrypted);

        // Then
        assertEquals(plaintext, decrypted);
    }
}


