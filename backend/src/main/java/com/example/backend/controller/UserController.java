package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(
            @RequestParam String query,
            Authentication authentication) {
        String currentUserEmail = authentication.getName();
        
        List<User> users = userRepository.findAll().stream()
            .filter(u -> u.getEmail().toLowerCase().contains(query.toLowerCase()) 
                      && !u.getEmail().equals(currentUserEmail))
            .limit(10)
            .collect(Collectors.toList());

        List<UserDTO> userDTOs = users.stream()
            .map(u -> new UserDTO(u.getId(), u.getEmail(), u.getRole()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getEmail(), user.getRole()));
    }

    public static class UserDTO {
        private Long id;
        private String email;
        private String role;

        public UserDTO() {}

        public UserDTO(Long id, String email, String role) {
            this.id = id;
            this.email = email;
            this.role = role;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
