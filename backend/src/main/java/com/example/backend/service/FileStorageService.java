package com.example.backend.service;

import com.example.backend.dto.ChatMessageDTO;
import com.example.backend.entity.Conversation;
import com.example.backend.entity.Message;
import com.example.backend.entity.User;
import com.example.backend.repo.ConversationRepository;
import com.example.backend.repo.MessageRepository;
import com.example.backend.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public ChatMessageDTO storeFile(MultipartFile file, Long conversationId, Long senderId, String caption) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Check if the file's name contains invalid characters
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Filename contains invalid path sequence " + originalFileName);
            }

            // Generate unique filename
            String fileExtension = "";
            if (originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Determine message type based on file content type
            String contentType = file.getContentType();
            String messageType = determineMessageType(contentType);

            // Create message entity
            Conversation conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));
            User sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Message message = new Message();
            message.setConversation(conversation);
            message.setSender(sender);
            message.setContent(caption != null ? caption : originalFileName);
            message.setMessageType(messageType);
            message.setFileUrl("/api/files/view/" + uniqueFileName);
            message.setFileName(originalFileName);
            message.setFileType(contentType);
            message.setFileSize(file.getSize());
            message.setCreatedAt(LocalDateTime.now());

            message = messageRepository.save(message);

            // Create DTO
            ChatMessageDTO messageDTO = new ChatMessageDTO(
                    message.getId(),
                    conversationId,
                    senderId,
                    sender.getEmail(),
                    message.getContent(),
                    messageType,
                    message.getFileUrl(),
                    message.getFileName(),
                    message.getFileType(),
                    message.getFileSize(),
                    message.getThumbnailUrl(),
                    message.getCreatedAt().toString(),
                    false
            );

            // Send via WebSocket
            messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, messageDTO);

            return messageDTO;

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + fileName, ex);
        }
    }

    private String determineMessageType(String contentType) {
        if (contentType == null) {
            return "FILE";
        }

        if (contentType.startsWith("image/")) {
            return "IMAGE";
        } else if (contentType.startsWith("video/")) {
            return "VIDEO";
        } else if (contentType.startsWith("audio/")) {
            return "AUDIO";
        } else if (contentType.equals("application/pdf") ||
                   contentType.equals("application/msword") ||
                   contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                   contentType.equals("application/vnd.ms-excel") ||
                   contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                   contentType.equals("text/plain")) {
            return "DOCUMENT";
        } else {
            return "FILE";
        }
    }
}
