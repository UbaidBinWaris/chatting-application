package com.example.backend.controller;

import com.example.backend.config.TestSecurityConfig;
import com.example.backend.dto.UserRegistrationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for UserController with real database
 */
@SpringBootTest
//@AutoConfigureMockMvc@Import(TestSecurityConfig.class)@Transactional
@DisplayName("UserController Integration Tests")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("integrationuser");
        registrationDto.setEmail("integration@example.com");
        registrationDto.setPassword("password123");
        registrationDto.setDisplayName("Integration Test User");
    }

    @Test
    @DisplayName("Full user lifecycle: register, retrieve, update status")
    void testFullUserLifecycle() throws Exception {
        // 1. Register a new user
        String registerResponse = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("integrationuser"))
                .andExpect(jsonPath("$.email").value("integration@example.com"))
                .andExpect(jsonPath("$.displayName").value("Integration Test User"))
                .andExpect(jsonPath("$.status").value("OFFLINE"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.isVerified").value(false))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract user ID from response
        Long userId = objectMapper.readTree(registerResponse).get("id").asLong();

        // 2. Retrieve the user by ID
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value("integrationuser"))
                .andExpect(jsonPath("$.email").value("integration@example.com"));

        // 3. Update user status to ONLINE
        mockMvc.perform(put("/api/users/" + userId + "/status")
                .param("status", "ONLINE"))
                .andExpect(status().isOk())
                .andExpect(content().string("Status updated successfully"));

        // 4. Verify status was updated
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ONLINE"))
                .andExpect(jsonPath("$.lastSeen").exists());
    }

    @Test
    @DisplayName("Should prevent duplicate username registration")
    void testDuplicateUsername() throws Exception {
        // Register first user
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated());

        // Try to register with same username but different email
        UserRegistrationDto duplicateDto = new UserRegistrationDto();
        duplicateDto.setUsername("integrationuser");
        duplicateDto.setEmail("different@example.com");
        duplicateDto.setPassword("password123");
        duplicateDto.setDisplayName("Different User");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));
    }

    @Test
    @DisplayName("Should prevent duplicate email registration")
    void testDuplicateEmail() throws Exception {
        // Register first user
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated());

        // Try to register with different username but same email
        UserRegistrationDto duplicateDto = new UserRegistrationDto();
        duplicateDto.setUsername("differentuser");
        duplicateDto.setEmail("integration@example.com");
        duplicateDto.setPassword("password123");
        duplicateDto.setDisplayName("Different User");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already exists"));
    }

    @Test
    @DisplayName("Should reject registration with short password")
    void testShortPassword() throws Exception {
        registrationDto.setPassword("short");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Password must be at least 8 characters")));
    }

    @Test
    @DisplayName("Should reject registration with empty username")
    void testEmptyUsername() throws Exception {
        registrationDto.setUsername("");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Username cannot be empty")));
    }

    @Test
    @DisplayName("Should reject registration with empty email")
    void testEmptyEmail() throws Exception {
        registrationDto.setEmail("");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email cannot be empty")));
    }

    @Test
    @DisplayName("Should return 404 for non-existent user")
    void testGetNonExistentUser() throws Exception {
        mockMvc.perform(get("/api/users/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update status for non-existent user")
    void testUpdateStatusNonExistentUser() throws Exception {
        mockMvc.perform(put("/api/users/99999/status")
                .param("status", "ONLINE"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User not found")));
    }

    @Test
    @DisplayName("Should handle case-insensitive username and email")
    void testCaseInsensitiveUserData() throws Exception {
        // Register with lowercase
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated());

        // Try to register with uppercase
        UserRegistrationDto uppercaseDto = new UserRegistrationDto();
        uppercaseDto.setUsername("INTEGRATIONUSER");
        uppercaseDto.setEmail("INTEGRATION@EXAMPLE.COM");
        uppercaseDto.setPassword("password123");
        uppercaseDto.setDisplayName("Uppercase User");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(uppercaseDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should register multiple users successfully")
    void testRegisterMultipleUsers() throws Exception {
        // Register first user
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated());

        // Register second user
        UserRegistrationDto secondDto = new UserRegistrationDto();
        secondDto.setUsername("seconduser");
        secondDto.setEmail("second@example.com");
        secondDto.setPassword("password456");
        secondDto.setDisplayName("Second User");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("seconduser"));

        // Register third user
        UserRegistrationDto thirdDto = new UserRegistrationDto();
        thirdDto.setUsername("thirduser");
        thirdDto.setEmail("third@example.com");
        thirdDto.setPassword("password789");
        thirdDto.setDisplayName("Third User");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(thirdDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("thirduser"));
    }

    @Test
    @DisplayName("Should handle special characters in email and username")
    void testSpecialCharactersInRegistration() throws Exception {
        UserRegistrationDto specialDto = new UserRegistrationDto();
        specialDto.setUsername("user_test-123");
        specialDto.setEmail("user+test@example.co.uk");
        specialDto.setPassword("P@ssw0rd!123");
        specialDto.setDisplayName("Test User's Name");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("user_test-123"))
                .andExpect(jsonPath("$.email").value("user+test@example.co.uk"))
                .andExpect(jsonPath("$.displayName").value("Test User's Name"));
    }

    @Test
    @DisplayName("Should persist user data correctly with encryption")
    void testDataPersistenceWithEncryption() throws Exception {
        // Register user
        String registerResponse = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = objectMapper.readTree(registerResponse).get("id").asLong();

        // Retrieve user multiple times to ensure decryption works consistently
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/api/users/" + userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("integrationuser"))
                    .andExpect(jsonPath("$.email").value("integration@example.com"))
                    .andExpect(jsonPath("$.displayName").value("Integration Test User"));
        }
    }

    @Test
    @DisplayName("Should update status multiple times")
    void testMultipleStatusUpdates() throws Exception {
        // Register user
        String registerResponse = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = objectMapper.readTree(registerResponse).get("id").asLong();

        // Update status multiple times
        String[] statuses = {"ONLINE", "AWAY", "BUSY", "OFFLINE", "ONLINE"};
        for (String status : statuses) {
            mockMvc.perform(put("/api/users/" + userId + "/status")
                    .param("status", status))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/users/" + userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(status));
        }
    }

    @Test
    @DisplayName("Should use default display name when not provided")
    void testDefaultDisplayName() throws Exception {
        registrationDto.setDisplayName(null);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.displayName").value("integrationuser"));
    }
}
