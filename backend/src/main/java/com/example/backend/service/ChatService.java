package com.example.backend.service;

import com.example.backend.dto.*;
import com.example.backend.entity.*;
import com.example.backend.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ConversationParticipantRepository participantRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageReadReceiptRepository readReceiptRepository;

    @Autowired
    private UserRepository userRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Transactional
    public ConversationDTO createDirectConversation(Long user1Id, Long user2Id) {
        // Check if conversation already exists
        var existing = conversationRepository.findDirectConversation(user1Id, user2Id);
        if (existing.isPresent()) {
            return convertToDTO(existing.get(), user1Id);
        }

        // Create new conversation
        Conversation conversation = new Conversation(null, false);
        conversation = conversationRepository.save(conversation);

        User user1 = userRepository.findById(user1Id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        User user2 = userRepository.findById(user2Id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Add participants
        ConversationParticipant p1 = new ConversationParticipant(conversation, user1, false);
        ConversationParticipant p2 = new ConversationParticipant(conversation, user2, false);
        participantRepository.save(p1);
        participantRepository.save(p2);

        return convertToDTO(conversation, user1Id);
    }

    @Transactional
    public ConversationDTO createGroupConversation(String name, List<Long> participantIds, Long creatorId) {
        Conversation conversation = new Conversation(name, true);
        conversation = conversationRepository.save(conversation);

        // Add creator as admin
        User creator = userRepository.findById(creatorId)
            .orElseThrow(() -> new RuntimeException("Creator not found"));
        ConversationParticipant creatorParticipant = new ConversationParticipant(conversation, creator, true);
        participantRepository.save(creatorParticipant);

        // Add other participants
        for (Long userId : participantIds) {
            if (!userId.equals(creatorId)) {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));
                ConversationParticipant participant = new ConversationParticipant(conversation, user, false);
                participantRepository.save(participant);
            }
        }

        return convertToDTO(conversation, creatorId);
    }

    public List<ConversationDTO> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findByUserId(userId);
        return conversations.stream()
            .map(conv -> convertToDTO(conv, userId))
            .collect(Collectors.toList());
    }

    @Transactional
    public ChatMessageDTO sendMessage(Long conversationId, Long senderId, String content, String messageType) {
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Sender not found"));

        // Verify sender is participant
        participantRepository.findByConversationIdAndUserId(conversationId, senderId)
            .orElseThrow(() -> new RuntimeException("User is not a participant"));

        Message message = new Message(conversation, sender, content);
        message.setMessageType(messageType != null ? messageType : "TEXT");
        message = messageRepository.save(message);

        return convertMessageToDTO(message);
    }

    public List<ChatMessageDTO> getMessages(Long conversationId, Long userId, int page, int size) {
        // Verify user is participant
        participantRepository.findByConversationIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new RuntimeException("User is not a participant"));

        var messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(
            conversationId, PageRequest.of(page, size));
        
        return messages.getContent().stream()
            .map(this::convertMessageToDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public void addParticipant(Long conversationId, Long userId, Long adminId) {
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.getIsGroup()) {
            throw new RuntimeException("Cannot add participants to direct conversation");
        }

        // Verify admin permissions
        ConversationParticipant admin = participantRepository.findByConversationIdAndUserId(conversationId, adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.getIsAdmin()) {
            throw new RuntimeException("Only admins can add participants");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if already participant
        if (participantRepository.findByConversationIdAndUserId(conversationId, userId).isPresent()) {
            throw new RuntimeException("User is already a participant");
        }

        ConversationParticipant participant = new ConversationParticipant(conversation, user, false);
        participantRepository.save(participant);
    }

    @Transactional
    public void removeParticipant(Long conversationId, Long userId, Long adminId) {
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.getIsGroup()) {
            throw new RuntimeException("Cannot remove participants from direct conversation");
        }

        // Verify admin permissions
        ConversationParticipant admin = participantRepository.findByConversationIdAndUserId(conversationId, adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.getIsAdmin()) {
            throw new RuntimeException("Only admins can remove participants");
        }

        participantRepository.deleteByConversationIdAndUserId(conversationId, userId);
    }

    @Transactional
    public void makeAdmin(Long conversationId, Long userId, Long adminId) {
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.getIsGroup()) {
            throw new RuntimeException("Cannot assign admins in direct conversation");
        }

        // Verify admin permissions
        ConversationParticipant admin = participantRepository.findByConversationIdAndUserId(conversationId, adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.getIsAdmin()) {
            throw new RuntimeException("Only admins can assign admin role");
        }

        ConversationParticipant participant = participantRepository.findByConversationIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new RuntimeException("Participant not found"));
        
        participant.setIsAdmin(true);
        participantRepository.save(participant);
    }

    private ConversationDTO convertToDTO(Conversation conversation, Long currentUserId) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(conversation.getId());
        dto.setName(conversation.getName());
        dto.setIsGroup(conversation.getIsGroup());
        dto.setCreatedAt(conversation.getCreatedAt().format(formatter));
        dto.setUpdatedAt(conversation.getUpdatedAt().format(formatter));

        // Get participants
        List<ConversationParticipant> participants = participantRepository.findByConversationId(conversation.getId());
        dto.setParticipants(participants.stream()
            .map(p -> new ParticipantDTO(
                p.getId(),
                p.getUser().getId(),
                p.getUser().getEmail(),
                p.getIsAdmin(),
                p.getJoinedAt().format(formatter)
            ))
            .collect(Collectors.toList()));

        // Get last message
        List<Message> lastMessages = messageRepository.findLatestMessages(conversation.getId(), PageRequest.of(0, 1));
        if (!lastMessages.isEmpty()) {
            dto.setLastMessage(convertMessageToDTO(lastMessages.get(0)));
        }

        // Get unread count
        Long unreadCount = messageRepository.countUnreadMessages(conversation.getId(), currentUserId);
        dto.setUnreadCount(unreadCount);

        return dto;
    }

    private ChatMessageDTO convertMessageToDTO(Message message) {
        return new ChatMessageDTO(
            message.getId(),
            message.getConversation().getId(),
            message.getSender().getId(),
            message.getSender().getEmail(),
            message.getContent(),
            message.getMessageType(),
            message.getFileUrl(),
            message.getFileName(),
            message.getFileType(),
            message.getFileSize(),
            message.getThumbnailUrl(),
            message.getCreatedAt().format(formatter),
            message.getIsRead()
        );
    }
}
