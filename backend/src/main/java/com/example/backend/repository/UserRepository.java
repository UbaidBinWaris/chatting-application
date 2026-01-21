package com.example.backend.repository;

import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by encrypted username
     * @param username encrypted username
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by encrypted email
     * @param email encrypted email
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username already exists (for registration validation)
     * @param username encrypted username
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if email already exists (for registration validation)
     * @param email encrypted email
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
}

