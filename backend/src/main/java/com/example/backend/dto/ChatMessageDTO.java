package com.example.backend.dto;

public class ChatMessageDTO {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderEmail;
    private String content;
    private String messageType;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String thumbnailUrl;
    private String createdAt;
    private Boolean isRead;

    public ChatMessageDTO() {}

    public ChatMessageDTO(Long id, Long conversationId, Long senderId, String senderEmail, 
                          String content, String messageType, String createdAt, Boolean isRead) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.senderEmail = senderEmail;
        this.content = content;
        this.messageType = messageType;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public ChatMessageDTO(Long id, Long conversationId, Long senderId, String senderEmail, 
                          String content, String messageType, String fileUrl, String fileName,
                          String fileType, Long fileSize, String thumbnailUrl, String createdAt, Boolean isRead) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.senderEmail = senderEmail;
        this.content = content;
        this.messageType = messageType;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
}
