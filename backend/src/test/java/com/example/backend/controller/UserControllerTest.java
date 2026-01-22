package com.example.backend.controller;

import com.example.backend.config.TestSecurityConfig;
import com.example.backend.dto.UserRegistrationDto;
import com.example.backend.dto.UserResponseDto;
import com.example.backend.model.User;
import com.example.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test cases for UserController
 */
@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser;
    private UserRegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("$2a$12$hashedpassword");
        testUser.setDisplayName("Test User");
        testUser.setStatus("OFFLINE");
        testUser.setIsActive(true);
        testUser.setIsVerified(false);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setLastSeen(LocalDateTime.now());

        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password123");
        registrationDto.setDisplayName("Test User");
    }

    @Test
    @DisplayName("Should register user successfully")
    void testRegisterUserSuccess() throws Exception {
        // Given
        when(userService.createUser(
            anyString(), 
            anyString(), 
            anyString(), 
            anyString()
        )).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.displayName").value("Test User"));

        verify(userService).createUser(
            "testuser",
            "test@example.com",
            "password123",
            "Test User"
        );
    }

    @Test
    @DisplayName("Should return bad request for duplicate username")
    void testRegisterUserDuplicateUsername() throws Exception {
        // Given
        when(userService.createUser(anyString(), anyString(), anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("Username already exists"));

        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));
    }

    @Test
    @DisplayName("Should return bad request for duplicate email")
    void testRegisterUserDuplicateEmail() throws Exception {
        // Given
        when(userService.createUser(anyString(), anyString(), anyString(), anyString()))
            .thenThrow(new IllegalArgumentException("Email already exists"));

        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists"));
    }

    @Test
    @DisplayName("Should return internal server error for unexpected exception")
    void testRegisterUserUnexpectedException() throws Exception {
        // Given
        when(userService.createUser(anyString(), anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error creating user")));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void testGetUserByIdSuccess() throws Exception {
        // Given
        when(userService.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.displayName").value("Test User"));

        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("Should return not found when user doesn't exist")
    void testGetUserByIdNotFound() throws Exception {
        // Given
        when(userService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

        verify(userService).findById(999L);
    }

    @Test
    @DisplayName("Should update user status successfully")
    void testUpdateStatusSuccess() throws Exception {
        // Given
        doNothing().when(userService).updateUserStatus(1L, "ONLINE");

        // When & Then
        mockMvc.perform(put("/api/users/1/status")
                .param("status", "ONLINE"))
                .andExpect(status().isOk())
                .andExpect(content().string("Status updated successfully"));

        verify(userService).updateUserStatus(1L, "ONLINE");
    }

    @Test
    @DisplayName("Should return bad request for invalid status update")
    void testUpdateStatusInvalidUser() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("User not found"))
            .when(userService).updateUserStatus(999L, "ONLINE");

        // When & Then
        mockMvc.perform(put("/api/users/999/status")
                .param("status", "ONLINE"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("Should handle registration with minimal data")
    void testRegisterUserMinimalData() throws Exception {
        // Given
        UserRegistrationDto minimalDto = new UserRegistrationDto();
        minimalDto.setUsername("minimaluser");
        minimalDto.setEmail("minimal@example.com");
        minimalDto.setPassword("password123");
        minimalDto.setDisplayName(null); // No display name

        User minimalUser = new User();
        minimalUser.setId(2L);
        minimalUser.setUsername("minimaluser");
        minimalUser.setEmail("minimal@example.com");
        minimalUser.setDisplayName("minimaluser");
        minimalUser.setStatus("OFFLINE");
        minimalUser.setIsActive(true);
        minimalUser.setIsVerified(false);
        minimalUser.setCreatedAt(LocalDateTime.now());

        when(userService.createUser(anyString(), anyString(), anyString(), isNull()))
            .thenReturn(minimalUser);

        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("minimaluser"));
    }

    @Test
    @DisplayName("Should handle special characters in registration")
    void testRegisterUserSpecialCharacters() throws Exception {
        // Given
        UserRegistrationDto specialDto = new UserRegistrationDto();
        specialDto.setUsername("user_special");
        specialDto.setEmail("special+test@example.com");
        specialDto.setPassword("P@ssw0rd!#$");
        specialDto.setDisplayName("User O'Brien");

        User specialUser = new User();
        specialUser.setId(3L);
        specialUser.setUsername("user_special");
        specialUser.setEmail("special+test@example.com");
        specialUser.setDisplayName("User O'Brien");
        specialUser.setStatus("OFFLINE");
        specialUser.setIsActive(true);
        specialUser.setIsVerified(false);
        specialUser.setCreatedAt(LocalDateTime.now());

        when(userService.createUser(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(specialUser);

        // When & Then
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("user_special"))
                .andExpect(jsonPath("$.email").value("special+test@example.com"));
    }

    @Test
    @DisplayName("Should update status to different values")
    void testUpdateStatusDifferentValues() throws Exception {
        // Test ONLINE
        mockMvc.perform(put("/api/users/1/status")
                .param("status", "ONLINE"))
                .andExpect(status().isOk());

        // Test AWAY
        mockMvc.perform(put("/api/users/1/status")
                .param("status", "AWAY"))
                .andExpect(status().isOk());

        // Test BUSY
        mockMvc.perform(put("/api/users/1/status")
                .param("status", "BUSY"))
                .andExpect(status().isOk());

        // Test OFFLINE
        mockMvc.perform(put("/api/users/1/status")
                .param("status", "OFFLINE"))
                .andExpect(status().isOk());

        verify(userService, times(4)).updateUserStatus(eq(1L), anyString());
    }
}
