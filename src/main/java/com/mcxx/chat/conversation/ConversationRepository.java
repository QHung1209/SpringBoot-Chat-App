package com.mcxx.chat.conversation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mcxx.chat.conversation.projection.ConversationMessageProjection;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

  Optional<Conversation> findByPairKey(String pairKey);

  @Query(value = """
      SELECT
        co.id,
        co.type,
        co.name,
        co.avatar_url,
        co.updated_at,
        co.last_message_id,
        m.content,
        m.sender_id
      FROM conversations co
      LEFT JOIN conversation_members cm ON co.id = cm.conversation_id
      LEFT JOIN messages m ON m.id = co.last_message_id
      WHERE cm.user_id = :userId
        AND (:updatedTime IS NULL OR co.updated_at < :updatedTime)
      ORDER BY co.updated_at DESC
      LIMIT 10
      """, nativeQuery = true)
  List<ConversationMessageProjection> getConversations(UUID userId, Instant updatedTime);


}
