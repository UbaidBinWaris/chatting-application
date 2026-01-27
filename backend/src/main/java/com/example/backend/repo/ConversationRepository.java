package com.example.backend.repo;

import com.example.backend.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.user.id = :userId ORDER BY c.updatedAt DESC")
    List<Conversation> findByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c WHERE c.isGroup = true AND c.id IN " +
           "(SELECT p.conversation.id FROM ConversationParticipant p WHERE p.user.id = :userId)")
    List<Conversation> findGroupsByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c WHERE c.isGroup = false AND c.id IN " +
           "(SELECT p1.conversation.id FROM ConversationParticipant p1 " +
           "WHERE p1.user.id = :user1Id AND p1.conversation.id IN " +
           "(SELECT p2.conversation.id FROM ConversationParticipant p2 WHERE p2.user.id = :user2Id))")
    Optional<Conversation> findDirectConversation(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
