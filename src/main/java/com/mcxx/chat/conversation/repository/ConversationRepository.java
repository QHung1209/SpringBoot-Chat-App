package com.mcxx.chat.conversation.repository;

import com.mcxx.chat.conversation.domain.Conversation;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

  Optional<Conversation> findByPairKey(String pairKey);

  @Query(value = """
      SELECT
        co.id AS id,
        co.type AS type,
        CASE
          WHEN co.type = 'DIRECT' THEN other_user.full_name
          ELSE co.name
        END AS name,
        CASE
          WHEN co.type = 'DIRECT' THEN other_user.avatar_url
          ELSE co.avatar_url
        END AS "avatarUrl",
        co.updated_at AS "updatedAt",
        m.content AS content,
        m.sender_id AS "senderId",
        co.last_message_id AS "lastMessageId"
      FROM conversations co
      JOIN conversation_members cm
        ON co.id = cm.conversation_id
        AND cm.user_id = :userId
      LEFT JOIN conversation_members other_cm
        ON co.type = 'DIRECT'
        AND other_cm.conversation_id = co.id
        AND other_cm.user_id <> :userId
      LEFT JOIN users other_user ON other_user.id = other_cm.user_id
      LEFT JOIN messages m ON m.id = co.last_message_id
      WHERE (cast(:updatedTime as timestamp) IS NULL OR co.updated_at < cast(:updatedTime as timestamp))
      ORDER BY co.updated_at DESC
      LIMIT 10
      """, nativeQuery = true)
  List<ConversationMessageProjection> getConversations(UUID userId, Instant updatedTime);


}
