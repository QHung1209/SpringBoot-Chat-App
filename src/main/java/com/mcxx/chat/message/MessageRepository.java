package com.mcxx.chat.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query(value = """
      SELECT id, content, conversation_id, sender_id, type, metadata, reply_to_message_id, media_id,
      is_pinned, deleted_at, created_at
      FROM messages
      WHERE conversation_id = :conversationId
      AND (:createdAt IS NULL OR created_at < :createdAt)
      ORDER BY created_at DESC
      LIMIT 15
      """, nativeQuery = true)
  public List<Message> findByConversationIdOrderByCreatedAtDesc(UUID conversationId,
      Instant createdAt);
}
