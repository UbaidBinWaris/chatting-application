package com.example.backend.dto;

public class ParticipantDTO {
    private Long id;
    private Long userId;
    private String userEmail;
    private Boolean isAdmin;
    private String joinedAt;

    public ParticipantDTO() {}

    public ParticipantDTO(Long id, Long userId, String userEmail, Boolean isAdmin, String joinedAt) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.isAdmin = isAdmin;
        this.joinedAt = joinedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }

    public String getJoinedAt() { return joinedAt; }
    public void setJoinedAt(String joinedAt) { this.joinedAt = joinedAt; }
}
