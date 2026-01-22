package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.EncryptionUtil;
import com.example.backend.security.PasswordHashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for managing users with encryption
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final EncryptionUtil encryptionUtil;
    private final PasswordHashUtil passwordHashUtil;

    public UserService(UserRepository userRepository,
                      EncryptionUtil encryptionUtil,
                      PasswordHashUtil passwordHashUtil) {
        this.userRepository = userRepository;
        this.encryptionUtil = encryptionUtil;
        this.passwordHashUtil = passwordHashUtil;
    }

    /**
     * Create a new user with encrypted username and email
     */
    @Transactional
    public User createUser(String username, String email, String password, String displayName) {
        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        // Encrypt username and email
        String encryptedUsername = encryptionUtil.encrypt(username.toLowerCase().trim());
        String encryptedEmail = encryptionUtil.encrypt(email.toLowerCase().trim());

        // Check if user already exists
        if (userRepository.existsByUsernameEncrypted(encryptedUsername)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmailEncrypted(encryptedEmail)) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Hash password
        String hashedPassword = passwordHashUtil.hashPassword(password);

        // Create user entity
        User user = new User();
        user.setUsernameEncrypted(encryptedUsername);
        user.setEmailEncrypted(encryptedEmail);
        user.setPasswordHash(hashedPassword);
        user.setDisplayName(displayName != null ? displayName : username);
        user.setStatus("OFFLINE");
        user.setIsActive(true);
        user.setIsVerified(false);

        // Save to database
        User savedUser = userRepository.save(user);

        // Set decrypted values for transient fields
        savedUser.setUsername(username);
        savedUser.setEmail(email);

        logger.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Find user by username (encrypts the search term)
     */
    public Optional<User> findByUsername(String username) {
        String encryptedUsername = encryptionUtil.encrypt(username.toLowerCase().trim());
        Optional<User> userOpt = userRepository.findByUsernameEncrypted(encryptedUsername);

        // Decrypt username and email for the returned user
        userOpt.ifPresent(this::decryptUserFields);

        return userOpt;
    }

    /**
     * Find user by email (encrypts the search term)
     */
    public Optional<User> findByEmail(String email) {
        String encryptedEmail = encryptionUtil.encrypt(email.toLowerCase().trim());
        Optional<User> userOpt = userRepository.findByEmailEncrypted(encryptedEmail);

        // Decrypt username and email for the returned user
        userOpt.ifPresent(this::decryptUserFields);

        return userOpt;
    }

    /**
     * Verify user login credentials
     */
    public boolean verifyCredentials(String username, String password) {
        Optional<User> userOpt = findByUsername(username);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        return passwordHashUtil.verifyPassword(password, user.getPasswordHash());
    }

    /**
     * Update user status
     */
    @Transactional
    public void updateUserStatus(Long userId, String status) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setStatus(status);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);

        logger.info("User {} status updated to {}", userId, status);
    }

    /**
     * Update user password
     */
    @Transactional
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verify old password
        if (!passwordHashUtil.verifyPassword(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid old password");
        }

        // Hash and save new password
        String newHashedPassword = passwordHashUtil.hashPassword(newPassword);
        user.setPasswordHash(newHashedPassword);
        userRepository.save(user);

        logger.info("Password updated for user {}", userId);
    }

    /**
     * Helper method to decrypt user fields
     */
    private void decryptUserFields(User user) {
        try {
            user.setUsername(encryptionUtil.decrypt(user.getUsernameEncrypted()));
            user.setEmail(encryptionUtil.decrypt(user.getEmailEncrypted()));
        } catch (Exception e) {
            logger.error("Error decrypting user fields for user ID: {}", user.getId(), e);
        }
    }

    /**
     * Get user by ID with decrypted fields
     */
    public Optional<User> findById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        userOpt.ifPresent(this::decryptUserFields);
        return userOpt;
    }
}

