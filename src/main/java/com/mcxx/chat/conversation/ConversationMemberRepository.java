package com.mcxx.chat.conversation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.mcxx.chat.conversation.constants.ConversationRole;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, UUID> {

  @Modifying
  @Query(value = """
      DELETE FROM conversation_members
      WHERE conversation_id = :conversationId AND user_id = :userId
      """, nativeQuery = true)
  void removeMember(UUID conversationId, UUID userId);

  @Modifying
  @Query(value = """
      UPDATE conversation_members
      SET role = :role
      WHERE conversation_id = :conversationId AND user_id = :userId
      """, nativeQuery = true)
  void updateRole(UUID conversationId, UUID userId, ConversationRole role);

  List<ConversationMember> findAllByConversationId(UUID conversationId);

  Optional<ConversationMember> findByConversationIdAndUserId(UUID conversationId, UUID userId);

  Long countByConversationIdAndRole(UUID conversationId, ConversationRole role);

  @Query(value = """
      SELECT
      FROM conversation_members
      WHERE conversation_id = :conversationId
      AND (created_at IS NULL OR created_at > :createdAt)
      ORDER BY created_at ASC
      LIMIT 30
      """, nativeQuery = true)
  List<ConversationMember> findAllByConversationIdOrderByCreatedAtAsc(UUID conversationId,
      Instant createdAt);

  Boolean existsByConversationIdAndUserId(UUID conversationId, UUID userId);
}
