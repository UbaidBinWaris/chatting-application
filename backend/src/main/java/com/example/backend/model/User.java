package com.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * User entity with encrypted sensitive fields
 * - username and email are stored encrypted
 * - password is stored as BCrypt hash
 * - Other fields are stored in plaintext
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Encrypted username (stored in database as encrypted text)
     */
    @Column(name = "username_encrypted", nullable = false, unique = true, columnDefinition = "TEXT")
    private String usernameEncrypted;

    /**
     * Encrypted email (stored in database as encrypted text)
     */
    @Column(name = "email_encrypted", nullable = false, unique = true, columnDefinition = "TEXT")
    private String emailEncrypted;

    /**
     * BCrypt hashed password
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Display name (public, not encrypted)
     */
    @Column(name = "display_name", length = 100)
    private String displayName;

    /**
     * User status: ONLINE, OFFLINE, AWAY, BUSY
     */
    @Column(name = "status", length = 20)
    private String status = "OFFLINE";

    /**
     * Account active flag
     */
    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Email verification flag
     */
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    /**
     * Account creation timestamp
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Last seen timestamp
     */
    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    /**
     * Transient fields (not stored in database)
     * These are decrypted values for application use
     */
    @Transient
    private String username;

    @Transient
    private String email;

    /**
     * JPA lifecycle callbacks
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

