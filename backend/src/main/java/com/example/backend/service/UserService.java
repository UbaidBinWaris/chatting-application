package com.example.backend.service;
}
    }
        userRepository.save(user);
        user.setStatus(status);
                .orElseThrow(() -> new RuntimeException("User not found"));
        User user = userRepository.findById(userId)
    public void updateUserStatus(Long userId, User.UserStatus status) {
    @Transactional
     */
     * @param status new status
     * @param userId user ID
     * Update user status
    /**

    }
        return encryptionUtil.decrypt(user.getEmail());
    public String getDecryptedEmail(User user) {
     */
     * @return decrypted email
     * @param user the user entity
     * Get decrypted email
    /**

    }
        return encryptionUtil.decrypt(user.getUsername());
    public String getDecryptedUsername(User user) {
     */
     * @return decrypted username
     * @param user the user entity
     * Get decrypted username
    /**

    }
        return passwordEncoder.matches(plainPassword, user.getPassword());
    public boolean verifyPassword(User user, String plainPassword) {
     */
     * @return true if password matches, false otherwise
     * @param plainPassword plain text password to verify
     * @param user the user entity
     * Verify user password
    /**

    }
        return userRepository.findByEmail(encryptedEmail);
        String encryptedEmail = encryptionUtil.encrypt(plainEmail);
    public Optional<User> findByEmail(String plainEmail) {
     */
     * @return Optional containing the user if found
     * @param plainEmail plain text email
     * Find user by plain text email
    /**

    }
        return userRepository.findByUsername(encryptedUsername);
        String encryptedUsername = encryptionUtil.encrypt(plainUsername);
    public Optional<User> findByUsername(String plainUsername) {
     */
     * @return Optional containing the user if found
     * @param plainUsername plain text username
     * Find user by plain text username
    /**

    }
        return userRepository.save(user);

        user.setStatus(User.UserStatus.OFFLINE);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(hashedPassword);
        user.setEmail(encryptedEmail);
        user.setUsername(encryptedUsername);
        User user = new User();
        // Create user

        String hashedPassword = passwordEncoder.encode(plainPassword);
        // Hash password

        }
            throw new RuntimeException("Email already exists");
        if (userRepository.existsByEmail(encryptedEmail)) {
        }
            throw new RuntimeException("Username already exists");
        if (userRepository.existsByUsername(encryptedUsername)) {
        // Check if user already exists

        String encryptedEmail = encryptionUtil.encrypt(plainEmail);
        String encryptedUsername = encryptionUtil.encrypt(plainUsername);
        // Encrypt username and email
    public User registerUser(String plainUsername, String plainEmail, String plainPassword, String phoneNumber) {
    @Transactional
     */
     * @return saved user entity
     * @param phoneNumber phone number
     * @param plainPassword plain text password
     * @param plainEmail plain text email
     * @param plainUsername plain text username
     * Register a new user with encrypted username/email and hashed password
    /**

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EncryptionUtil encryptionUtil;
    private final UserRepository userRepository;

public class UserService {
@RequiredArgsConstructor
@Service

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.RequiredArgsConstructor;
import com.example.backend.util.EncryptionUtil;
import com.example.backend.repository.UserRepository;
import com.example.backend.entity.User;


