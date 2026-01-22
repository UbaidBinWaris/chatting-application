package com.example.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-GCM encryption utility for securing sensitive data
 * Uses randomly generated encryption key stored securely
 */
@Component
public class EncryptionUtil {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256; // AES-256
    private static final int GCM_TAG_LENGTH = 128; // 128-bit authentication tag
    private static final int GCM_IV_LENGTH = 12; // 12 bytes IV for GCM

    private final SecretKey secretKey;
    private final SecureRandom secureRandom;

    /**
     * Constructor - initializes with a randomly generated key
     * In production, load this key from secure storage (e.g., AWS KMS, HashiCorp Vault, environment variable)
     */
    public EncryptionUtil() {
        this.secureRandom = new SecureRandom();
        this.secretKey = generateSecretKey();
        logger.info("EncryptionUtil initialized with AES-256-GCM encryption");
    }

    /**
     * Constructor with custom key (for testing or loading from secure storage)
     */
    public EncryptionUtil(String base64Key) {
        this.secureRandom = new SecureRandom();
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        this.secretKey = new SecretKeySpec(decodedKey, ALGORITHM);
        logger.info("EncryptionUtil initialized with provided key");
    }

    /**
     * Generate a random AES-256 secret key
     */
    private SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_SIZE, secureRandom);
            SecretKey key = keyGenerator.generateKey();

            // Log the key in Base64 format (ONLY for development - store this securely!)
            String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
            logger.warn("=================================================");
            logger.warn("ENCRYPTION KEY (Store this securely!):");
            logger.warn(encodedKey);
            logger.warn("=================================================");

            return key;
        } catch (Exception e) {
            logger.error("Error generating secret key", e);
            throw new RuntimeException("Failed to generate encryption key", e);
        }
    }

    /**
     * Encrypt plaintext using AES-256-GCM
     *
     * @param plaintext The text to encrypt
     * @return Base64 encoded encrypted data with IV prepended
     */
    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return null;
        }

        try {
            // Generate random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

            // Encrypt
            byte[] encryptedData = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Combine IV + encrypted data
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedData);

            // Return as Base64 encoded string
            return Base64.getEncoder().encodeToString(byteBuffer.array());

        } catch (Exception e) {
            logger.error("Error encrypting data", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypt ciphertext using AES-256-GCM
     *
     * @param ciphertext Base64 encoded encrypted data with IV prepended
     * @return Decrypted plaintext
     */
    public String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.isEmpty()) {
            return null;
        }

        try {
            // Decode from Base64
            byte[] encryptedDataWithIv = Base64.getDecoder().decode(ciphertext);

            // Extract IV and encrypted data
            ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedDataWithIv);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            byte[] encryptedData = new byte[byteBuffer.remaining()];
            byteBuffer.get(encryptedData);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

            // Decrypt
            byte[] decryptedData = cipher.doFinal(encryptedData);

            return new String(decryptedData, StandardCharsets.UTF_8);

        } catch (Exception e) {
            logger.error("Error decrypting data", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * Get the encryption key as Base64 (for secure storage)
     * WARNING: Only use this to save the key to secure storage (e.g., environment variable, vault)
     */
    public String getKeyAsBase64() {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
}

