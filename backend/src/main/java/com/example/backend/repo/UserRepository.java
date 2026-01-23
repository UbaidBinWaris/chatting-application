package com.example.backend.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByRole(String role);
}
