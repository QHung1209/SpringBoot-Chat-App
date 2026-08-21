package com.mcxx.chat.chat.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mcxx.chat.chat.domain.Message;
import com.mcxx.chat.chat.domain.MessageType;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query(value = """
      SELECT
          m.id,
          m.content,
          m.conversation_id AS conversationId,
          m.sender_id AS senderId,
          m.type,
          CAST(m.metadata AS text) AS metadata,
          m.is_pinned AS isPinned,
          m.deleted_at AS deletedAt,
          m.created_at AS createdAt,
          m.updated_at AS updatedAt,

          rm.id AS replyId,
          rm.content AS replyContent,
          rm.sender_id AS replySenderId,
          rm.type AS replyType,
          rm.deleted_at AS replyDeletedAt,
          rm.created_at AS replyCreatedAt

      FROM messages m
      LEFT JOIN messages rm
          ON rm.id = m.reply_to_message_id
      WHERE m.conversation_id = :conversationId
        AND (
            CAST(:before AS timestamptz) IS NULL
            OR m.created_at < CAST(:before AS timestamptz)
        )
        AND (
            CAST(:after AS timestamptz) IS NULL
            OR m.created_at > CAST(:after AS timestamptz)
        )
      ORDER BY m.created_at DESC
      LIMIT 20
               """, nativeQuery = true)
  public List<MessageWithReplyProjection> findByConversationIdOrderByCreatedAtDesc(
      UUID conversationId, Instant before, Instant after);

  @Query("""
          SELECT m FROM Message m
          WHERE (cast(:type as string) IS NULL OR m.type = :type)
            AND m.conversationId = :conversationId
            AND (cast(:before as Instant) IS NULL OR m.createdAt < :before)
            AND m.medias IS NOT EMPTY
          ORDER BY m.createdAt DESC
          LIMIT 20
      """)
  List<Message> findMediaMessages(MessageType type, UUID conversationId, Instant before);

  @Query("""
          SELECT m FROM Message m
          WHERE m.conversationId = :conversationId
            AND (cast(:before as Instant) IS NULL OR m.createdAt < :before)
            AND m.isPinned = TRUE
          ORDER BY m.createdAt DESC
          LIMIT 20
      """)
  List<Message> findPinnedMessages(UUID conversationId, Instant before);
}
