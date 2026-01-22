package com.example.backend.service;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.EncryptionUtil;
import com.example.backend.security.PasswordHashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test cases for UserService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EncryptionUtil encryptionUtil;

    @Mock
    private PasswordHashUtil passwordHashUtil;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsernameEncrypted("encrypted_testuser");
        testUser.setEmailEncrypted("encrypted_test@example.com");
        testUser.setPasswordHash("$2a$12$hashedpassword");
        testUser.setDisplayName("Test User");
        testUser.setStatus("OFFLINE");
        testUser.setIsActive(true);
        testUser.setIsVerified(false);
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create user successfully")
    void testCreateUserSuccess() {
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        String displayName = "Test User";

        when(encryptionUtil.encrypt("testuser")).thenReturn("encrypted_testuser");
        when(encryptionUtil.encrypt("test@example.com")).thenReturn("encrypted_test@example.com");
        when(passwordHashUtil.hashPassword(password)).thenReturn("$2a$12$hashedpassword");
        when(userRepository.existsByUsernameEncrypted("encrypted_testuser")).thenReturn(false);
        when(userRepository.existsByEmailEncrypted("encrypted_test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User createdUser = userService.createUser(username, email, password, displayName);

        // Then
        assertNotNull(createdUser);
        assertEquals(1L, createdUser.getId());
        verify(encryptionUtil).encrypt("testuser");
        verify(encryptionUtil).encrypt("test@example.com");
        verify(passwordHashUtil).hashPassword(password);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception for empty username")
    void testCreateUserEmptyUsername() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("", "test@example.com", "password123", "Test");
        });
        
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception for null username")
    void testCreateUserNullUsername() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(null, "test@example.com", "password123", "Test");
        });
        
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception for empty email")
    void testCreateUserEmptyEmail() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("testuser", "", "password123", "Test");
        });
        
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception for short password")
    void testCreateUserShortPassword() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("testuser", "test@example.com", "short", "Test");
        });
        
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void testCreateUserDuplicateUsername() {
        // Given
        when(encryptionUtil.encrypt("testuser")).thenReturn("encrypted_testuser");
        when(encryptionUtil.encrypt("test@example.com")).thenReturn("encrypted_test@example.com");
        when(userRepository.existsByUsernameEncrypted("encrypted_testuser")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("testuser", "test@example.com", "password123", "Test");
        });
        
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testCreateUserDuplicateEmail() {
        // Given
        when(encryptionUtil.encrypt("testuser")).thenReturn("encrypted_testuser");
        when(encryptionUtil.encrypt("test@example.com")).thenReturn("encrypted_test@example.com");
        when(userRepository.existsByUsernameEncrypted("encrypted_testuser")).thenReturn(false);
        when(userRepository.existsByEmailEncrypted("encrypted_test@example.com")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser("testuser", "test@example.com", "password123", "Test");
        });
        
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find user by username successfully")
    void testFindByUsernameSuccess() {
        // Given
        when(encryptionUtil.encrypt("testuser")).thenReturn("encrypted_testuser");
        when(userRepository.findByUsernameEncrypted("encrypted_testuser")).thenReturn(Optional.of(testUser));
        when(encryptionUtil.decrypt("encrypted_testuser")).thenReturn("testuser");
        when(encryptionUtil.decrypt("encrypted_test@example.com")).thenReturn("test@example.com");

        // When
        Optional<User> foundUser = userService.findByUsername("testuser");

        // Then
        assertTrue(foundUser.isPresent());
        verify(encryptionUtil).encrypt("testuser");
        verify(encryptionUtil).decrypt("encrypted_testuser");
        verify(encryptionUtil).decrypt("encrypted_test@example.com");
    }

    @Test
    @DisplayName("Should return empty when username not found")
    void testFindByUsernameNotFound() {
        // Given
        when(encryptionUtil.encrypt("nonexistent")).thenReturn("encrypted_nonexistent");
        when(userRepository.findByUsernameEncrypted("encrypted_nonexistent")).thenReturn(Optional.empty());

        // When
        Optional<User> foundUser = userService.findByUsername("nonexistent");

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void testFindByEmailSuccess() {
        // Given
        when(encryptionUtil.encrypt("test@example.com")).thenReturn("encrypted_test@example.com");
        when(userRepository.findByEmailEncrypted("encrypted_test@example.com")).thenReturn(Optional.of(testUser));
        when(encryptionUtil.decrypt("encrypted_testuser")).thenReturn("testuser");
        when(encryptionUtil.decrypt("encrypted_test@example.com")).thenReturn("test@example.com");

        // When
        Optional<User> foundUser = userService.findByEmail("test@example.com");

        // Then
        assertTrue(foundUser.isPresent());
        verify(encryptionUtil).encrypt("test@example.com");
    }

    @Test
    @DisplayName("Should verify credentials successfully")
    void testVerifyCredentialsSuccess() {
        // Given
        when(encryptionUtil.encrypt("testuser")).thenReturn("encrypted_testuser");
        when(userRepository.findByUsernameEncrypted("encrypted_testuser")).thenReturn(Optional.of(testUser));
        when(encryptionUtil.decrypt(anyString())).thenReturn("decrypted");
        when(passwordHashUtil.verifyPassword("password123", testUser.getPasswordHash())).thenReturn(true);

        // When
        boolean result = userService.verifyCredentials("testuser", "password123");

        // Then
        assertTrue(result);
        verify(passwordHashUtil).verifyPassword("password123", testUser.getPasswordHash());
    }

    @Test
    @DisplayName("Should fail verification for wrong password")
    void testVerifyCredentialsWrongPassword() {
        // Given
        when(encryptionUtil.encrypt("testuser")).thenReturn("encrypted_testuser");
        when(userRepository.findByUsernameEncrypted("encrypted_testuser")).thenReturn(Optional.of(testUser));
        when(encryptionUtil.decrypt(anyString())).thenReturn("decrypted");
        when(passwordHashUtil.verifyPassword("wrongpassword", testUser.getPasswordHash())).thenReturn(false);

        // When
        boolean result = userService.verifyCredentials("testuser", "wrongpassword");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should fail verification for non-existent user")
    void testVerifyCredentialsUserNotFound() {
        // Given
        when(encryptionUtil.encrypt("nonexistent")).thenReturn("encrypted_nonexistent");
        when(userRepository.findByUsernameEncrypted("encrypted_nonexistent")).thenReturn(Optional.empty());

        // When
        boolean result = userService.verifyCredentials("nonexistent", "password123");

        // Then
        assertFalse(result);
        verify(passwordHashUtil, never()).verifyPassword(anyString(), anyString());
    }

    @Test
    @DisplayName("Should update user status successfully")
    void testUpdateUserStatus() {
        // Given
        Long userId = 1L;
        String newStatus = "ONLINE";
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateUserStatus(userId, newStatus);

        // Then
        verify(userRepository).findById(userId);
        verify(userRepository).save(testUser);
        assertEquals(newStatus, testUser.getStatus());
        assertNotNull(testUser.getLastSeen());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user status")
    void testUpdateUserStatusNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUserStatus(userId, "ONLINE");
        });
        
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update password successfully")
    void testUpdatePasswordSuccess() {
        // Given
        Long userId = 1L;
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword456";
        String newHash = "$2a$12$newhash";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordHashUtil.verifyPassword(oldPassword, testUser.getPasswordHash())).thenReturn(true);
        when(passwordHashUtil.hashPassword(newPassword)).thenReturn(newHash);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        userService.updatePassword(userId, oldPassword, newPassword);

        // Then
        verify(passwordHashUtil).verifyPassword(oldPassword, testUser.getPasswordHash());
        verify(passwordHashUtil).hashPassword(newPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid old password")
    void testUpdatePasswordInvalidOldPassword() {
        // Given
        Long userId = 1L;
        String oldPassword = "wrongOldPassword";
        String newPassword = "newPassword456";

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordHashUtil.verifyPassword(oldPassword, testUser.getPasswordHash())).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updatePassword(userId, oldPassword, newPassword);
        });
        
        assertEquals("Invalid old password", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find user by ID with decrypted fields")
    void testFindByIdSuccess() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(encryptionUtil.decrypt("encrypted_testuser")).thenReturn("testuser");
        when(encryptionUtil.decrypt("encrypted_test@example.com")).thenReturn("test@example.com");

        // When
        Optional<User> foundUser = userService.findById(userId);

        // Then
        assertTrue(foundUser.isPresent());
        verify(encryptionUtil).decrypt("encrypted_testuser");
        verify(encryptionUtil).decrypt("encrypted_test@example.com");
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void testFindByIdNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<User> foundUser = userService.findById(userId);

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should use default display name if not provided")
    void testCreateUserDefaultDisplayName() {
        // Given
        String username = "testuser";
        when(encryptionUtil.encrypt(username)).thenReturn("encrypted_testuser");
        when(encryptionUtil.encrypt("test@example.com")).thenReturn("encrypted_test@example.com");
        when(passwordHashUtil.hashPassword(anyString())).thenReturn("$2a$12$hashedpassword");
        when(userRepository.existsByUsernameEncrypted(anyString())).thenReturn(false);
        when(userRepository.existsByEmailEncrypted(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        // When
        User createdUser = userService.createUser(username, "test@example.com", "password123", null);

        // Then
        assertNotNull(createdUser);
        verify(userRepository).save(argThat(user -> 
            username.equals(user.getDisplayName())
        ));
    }
}
