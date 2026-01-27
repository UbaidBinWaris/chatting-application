package com.example.backend.dto;

import java.util.List;

public class ConversationDTO {
    private Long id;
    private String name;
    private Boolean isGroup;
    private List<ParticipantDTO> participants;
    private ChatMessageDTO lastMessage;
    private Long unreadCount;
    private String createdAt;
    private String updatedAt;

    public ConversationDTO() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Boolean getIsGroup() { return isGroup; }
    public void setIsGroup(Boolean isGroup) { this.isGroup = isGroup; }

    public List<ParticipantDTO> getParticipants() { return participants; }
    public void setParticipants(List<ParticipantDTO> participants) { this.participants = participants; }

    public ChatMessageDTO getLastMessage() { return lastMessage; }
    public void setLastMessage(ChatMessageDTO lastMessage) { this.lastMessage = lastMessage; }

    public Long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Long unreadCount) { this.unreadCount = unreadCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
