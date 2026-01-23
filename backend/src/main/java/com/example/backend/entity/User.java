package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    private String role; // "ADMIN", "USER", etc.

    @Column(name = "privilege_level", nullable=false)
    private int privilegeLevel; // 1, 2, 3

    public User() {}

    public User(String email, String password, String role, int privilegeLevel) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.privilegeLevel = privilegeLevel;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public int getPrivilegeLevel() { return privilegeLevel; }

    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setPrivilegeLevel(int privilegeLevel) { this.privilegeLevel = privilegeLevel; }
}
