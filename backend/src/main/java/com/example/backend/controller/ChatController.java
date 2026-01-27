package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.entity.User;
import com.example.backend.repo.UserRepository;
import com.example.backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> getConversations(Authentication authentication) {
        User user = getUserFromAuth(authentication);
        List<ConversationDTO> conversations = chatService.getUserConversations(user.getId());
        return ResponseEntity.ok(conversations);
    }

    @PostMapping("/conversations/direct")
    public ResponseEntity<ConversationDTO> createDirectConversation(
            @RequestParam Long otherUserId,
            Authentication authentication) {
        User user = getUserFromAuth(authentication);
        ConversationDTO conversation = chatService.createDirectConversation(user.getId(), otherUserId);
        return ResponseEntity.ok(conversation);
    }

    @PostMapping("/conversations/group")
    public ResponseEntity<ConversationDTO> createGroupConversation(
            @RequestBody CreateGroupRequest request,
            Authentication authentication) {
        User user = getUserFromAuth(authentication);
        ConversationDTO conversation = chatService.createGroupConversation(
            request.getName(),
            request.getParticipantIds(),
            user.getId()
        );
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication) {
        User user = getUserFromAuth(authentication);
        List<ChatMessageDTO> messages = chatService.getMessages(conversationId, user.getId(), page, size);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageDTO> sendMessage(
            @RequestBody SendMessageRequest request,
            Authentication authentication) {
        User user = getUserFromAuth(authentication);
        ChatMessageDTO message = chatService.sendMessage(
            request.getConversationId(),
            user.getId(),
            request.getContent(),
            request.getMessageType()
        );
        return ResponseEntity.ok(message);
    }

    @PostMapping("/conversations/{conversationId}/participants")
    public ResponseEntity<Void> addParticipant(
            @PathVariable Long conversationId,
            @RequestParam Long userId,
            Authentication authentication) {
        User admin = getUserFromAuth(authentication);
        chatService.addParticipant(conversationId, userId, admin.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/conversations/{conversationId}/participants/{userId}")
    public ResponseEntity<Void> removeParticipant(
            @PathVariable Long conversationId,
            @PathVariable Long userId,
            Authentication authentication) {
        User admin = getUserFromAuth(authentication);
        chatService.removeParticipant(conversationId, userId, admin.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/conversations/{conversationId}/participants/{userId}/admin")
    public ResponseEntity<Void> makeAdmin(
            @PathVariable Long conversationId,
            @PathVariable Long userId,
            Authentication authentication) {
        User admin = getUserFromAuth(authentication);
        chatService.makeAdmin(conversationId, userId, admin.getId());
        return ResponseEntity.ok().build();
    }

    private User getUserFromAuth(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
