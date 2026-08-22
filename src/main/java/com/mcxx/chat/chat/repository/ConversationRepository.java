package com.mcxx.chat.chat.repository;

import com.mcxx.chat.chat.domain.Conversation;
import com.mcxx.chat.chat.repository.projection.ConversationMessageProjection;
import com.mcxx.chat.chat.repository.projection.CountUnreadProjection;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

  Optional<Conversation> findByPairKey(String pairKey);

  @Query(
      value = """
          SELECT
            co.id AS id,
            co.type AS type,
            CASE
              WHEN co.type = 'DIRECT' THEN other_user.first_name
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
          WHERE (cast(:updatedTime AS timestamptz) IS NULL OR co.updated_at < cast(:updatedTime AS timestamptz))
          ORDER BY co.updated_at DESC
          LIMIT 10
          """,
      nativeQuery = true)
  List<ConversationMessageProjection> getConversations(UUID userId, Instant updatedTime);

  @Modifying
  @Query("""
      UPDATE Conversation c
      SET
        c.lastMessageId = :messageId,
        c.updatedAt = CURRENT_TIMESTAMP
      WHERE c.id = :conversationId
      """)
  void updateLastMessageId(UUID conversationId, UUID messageId);

  @Query(value = """
      SELECT m.conversation_id AS "conversationId", COUNT(m.id)
      FROM messages m
      JOIN conversation_members cm
        ON m.conversation_id = cm.conversation_id
      WHERE m.conversation_id IN (:conversationIds)
        AND cm.user_id = :userId
        AND m.sender_id <> :userId
        AND (cm.last_read_message_id IS NULL OR m.id > cm.last_read_message_id)
        AND (cm.hidden_at_message_id IS NULL OR m.id > cm.hidden_at_message_id)
      GROUP BY m.conversation_id
      """, nativeQuery = true)
  List<CountUnreadProjection> countUnread(List<UUID> conversationIds, UUID userId);

  @Modifying
  @Query("""
        UPDATE ConversationMember cm
        SET cm.hiddenAtMessageId = :lastMessageId
        WHERE cm.conversationId = :conversationId AND cm.userId = :userId
        """)
  void deleteConversation(UUID conversationId, UUID userId, UUID lastMessageId);
}
