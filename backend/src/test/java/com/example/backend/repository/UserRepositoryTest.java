package com.example.backend.repository;

import com.example.backend.model.User;
import com.example.backend.security.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for UserRepository
 */
@DataJpaTest
@Import(EncryptionUtil.class)
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionUtil encryptionUtil;

    private User testUser;
    private String encryptedUsername;
    private String encryptedEmail;

    @BeforeEach
    void setUp() {
        // Prepare encrypted data
        encryptedUsername = encryptionUtil.encrypt("testuser");
        encryptedEmail = encryptionUtil.encrypt("test@example.com");

        // Create test user
        testUser = new User();
        testUser.setUsernameEncrypted(encryptedUsername);
        testUser.setEmailEncrypted(encryptedEmail);
        testUser.setPasswordHash("$2a$12$hashedpassword");
        testUser.setDisplayName("Test User");
        testUser.setStatus("OFFLINE");
        testUser.setIsActive(true);
        testUser.setIsVerified(false);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save user successfully")
    void testSaveUser() {
        // When
        User savedUser = userRepository.save(testUser);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals(testUser.getUsernameEncrypted(), savedUser.getUsernameEncrypted());
        assertEquals(testUser.getEmailEncrypted(), savedUser.getEmailEncrypted());
    }

    @Test
    @DisplayName("Should find user by encrypted username")
    void testFindByUsernameEncrypted() {
        // Given
        entityManager.persist(testUser);
        entityManager.flush();

        // When
        Optional<User> foundUser = userRepository.findByUsernameEncrypted(encryptedUsername);

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getUsernameEncrypted(), foundUser.get().getUsernameEncrypted());
    }

    @Test
    @DisplayName("Should find user by encrypted email")
    void testFindByEmailEncrypted() {
        // Given
        entityManager.persist(testUser);
        entityManager.flush();

        // When
        Optional<User> foundUser = userRepository.findByEmailEncrypted(encryptedEmail);

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getEmailEncrypted(), foundUser.get().getEmailEncrypted());
    }

    @Test
    @DisplayName("Should return empty when username not found")
    void testFindByUsernameEncryptedNotFound() {
        // Given
        String nonExistentEncrypted = encryptionUtil.encrypt("nonexistent");

        // When
        Optional<User> foundUser = userRepository.findByUsernameEncrypted(nonExistentEncrypted);

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should check if encrypted username exists")
    void testExistsByUsernameEncrypted() {
        // Given
        entityManager.persist(testUser);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByUsernameEncrypted(encryptedUsername);
        boolean notExists = userRepository.existsByUsernameEncrypted(encryptionUtil.encrypt("other"));

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    @DisplayName("Should check if encrypted email exists")
    void testExistsByEmailEncrypted() {
        // Given
        entityManager.persist(testUser);
        entityManager.flush();

        // When
        boolean exists = userRepository.existsByEmailEncrypted(encryptedEmail);
        boolean notExists = userRepository.existsByEmailEncrypted(encryptionUtil.encrypt("other@example.com"));

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    @DisplayName("Should find user by ID")
    void testFindById() {
        // Given
        User savedUser = entityManager.persist(testUser);
        entityManager.flush();

        // When
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
    }

    @Test
    @DisplayName("Should update user status")
    void testUpdateUserStatus() {
        // Given
        User savedUser = entityManager.persist(testUser);
        entityManager.flush();
        entityManager.clear();

        // When
        Optional<User> userOpt = userRepository.findById(savedUser.getId());
        assertTrue(userOpt.isPresent());
        
        User user = userOpt.get();
        user.setStatus("ONLINE");
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<User> updatedUser = userRepository.findById(savedUser.getId());
        assertTrue(updatedUser.isPresent());
        assertEquals("ONLINE", updatedUser.get().getStatus());
        assertNotNull(updatedUser.get().getLastSeen());
    }

    @Test
    @DisplayName("Should delete user")
    void testDeleteUser() {
        // Given
        User savedUser = entityManager.persist(testUser);
        entityManager.flush();
        Long userId = savedUser.getId();

        // When
        userRepository.delete(savedUser);
        entityManager.flush();

        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Should enforce unique constraint on encrypted username")
    void testUniqueUsernameConstraint() {
        // Given
        entityManager.persist(testUser);
        entityManager.flush();

        // Create duplicate user
        User duplicateUser = new User();
        duplicateUser.setUsernameEncrypted(encryptedUsername);
        duplicateUser.setEmailEncrypted(encryptionUtil.encrypt("different@example.com"));
        duplicateUser.setPasswordHash("$2a$12$hashedpassword");
        duplicateUser.setDisplayName("Duplicate User");
        duplicateUser.setStatus("OFFLINE");
        duplicateUser.setIsActive(true);
        duplicateUser.setIsVerified(false);
        duplicateUser.setCreatedAt(LocalDateTime.now());

        // When & Then
        assertThrows(Exception.class, () -> {
            entityManager.persist(duplicateUser);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Should count all users")
    void testCountUsers() {
        // Given
        entityManager.persist(testUser);
        
        User anotherUser = new User();
        anotherUser.setUsernameEncrypted(encryptionUtil.encrypt("anotheruser"));
        anotherUser.setEmailEncrypted(encryptionUtil.encrypt("another@example.com"));
        anotherUser.setPasswordHash("$2a$12$hashedpassword");
        anotherUser.setDisplayName("Another User");
        anotherUser.setStatus("OFFLINE");
        anotherUser.setIsActive(true);
        anotherUser.setIsVerified(false);
        anotherUser.setCreatedAt(LocalDateTime.now());
        entityManager.persist(anotherUser);
        
        entityManager.flush();

        // When
        long count = userRepository.count();

        // Then
        assertEquals(2, count);
    }
}
