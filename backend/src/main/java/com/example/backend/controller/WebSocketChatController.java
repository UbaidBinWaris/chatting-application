package com.example.backend.controller;

import com.example.backend.dto.ChatMessageDTO;
import com.example.backend.dto.SendMessageRequest;
import com.example.backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request, Principal principal) {
        try {
            // Extract user ID from principal (email)
            String email = principal.getName();
            Long senderId = getUserIdFromEmail(email);

            // Save message
            ChatMessageDTO message = chatService.sendMessage(
                request.getConversationId(),
                senderId,
                request.getContent(),
                request.getMessageType()
            );

            // Send to conversation topic
            messagingTemplate.convertAndSend(
                "/topic/conversation." + request.getConversationId(),
                message
            );
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @MessageMapping("/chat.typing")
    public void sendTypingIndicator(@Payload TypingIndicator indicator, Principal principal) {
        messagingTemplate.convertAndSend(
            "/topic/conversation." + indicator.getConversationId() + ".typing",
            new TypingIndicator(indicator.getConversationId(), principal.getName(), indicator.getIsTyping())
        );
    }

    private Long getUserIdFromEmail(String email) {
        // This should be implemented to fetch user ID from email
        // For now, returning a placeholder
        return 1L; // TODO: Implement proper user lookup
    }

    public static class TypingIndicator {
        private Long conversationId;
        private String userEmail;
        private Boolean isTyping;

        public TypingIndicator() {}

        public TypingIndicator(Long conversationId, String userEmail, Boolean isTyping) {
            this.conversationId = conversationId;
            this.userEmail = userEmail;
            this.isTyping = isTyping;
        }

        public Long getConversationId() { return conversationId; }
        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

        public Boolean getIsTyping() { return isTyping; }
        public void setIsTyping(Boolean isTyping) { this.isTyping = isTyping; }
    }
}
