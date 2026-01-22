package com.example.backend.repository;

import com.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by encrypted username
     */
    Optional<User> findByUsernameEncrypted(String usernameEncrypted);

    /**
     * Find user by encrypted email
     */
    Optional<User> findByEmailEncrypted(String emailEncrypted);

    /**
     * Check if encrypted username exists
     */
    boolean existsByUsernameEncrypted(String usernameEncrypted);

    /**
     * Check if encrypted email exists
     */
    boolean existsByEmailEncrypted(String emailEncrypted);
}

