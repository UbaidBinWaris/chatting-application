package com.example.backend.dto;

public class LoginResponse {
    private String token;
    private String email;
    private String role;
    private int privilegeLevel;

    public LoginResponse() {}

    public LoginResponse(String token, String email, String role, int privilegeLevel) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.privilegeLevel = privilegeLevel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getPrivilegeLevel() {
        return privilegeLevel;
    }

    public void setPrivilegeLevel(int privilegeLevel) {
        this.privilegeLevel = privilegeLevel;
    }
}
