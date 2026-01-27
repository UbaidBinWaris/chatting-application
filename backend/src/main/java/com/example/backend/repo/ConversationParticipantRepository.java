package com.example.backend.repo;

import com.example.backend.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {

    List<ConversationParticipant> findByConversationId(Long conversationId);

    @Query("SELECT p FROM ConversationParticipant p WHERE p.conversation.id = :conversationId AND p.user.id = :userId")
    Optional<ConversationParticipant> findByConversationIdAndUserId(
        @Param("conversationId") Long conversationId, 
        @Param("userId") Long userId
    );

    @Query("SELECT p FROM ConversationParticipant p WHERE p.conversation.id = :conversationId AND p.isAdmin = true")
    List<ConversationParticipant> findAdminsByConversationId(@Param("conversationId") Long conversationId);

    @Query("SELECT COUNT(p) FROM ConversationParticipant p WHERE p.conversation.id = :conversationId")
    Long countByConversationId(@Param("conversationId") Long conversationId);

    void deleteByConversationIdAndUserId(Long conversationId, Long userId);
}
