package com.mcxx.chat.chat.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mcxx.chat.chat.domain.Message;

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

    @Query(value = """
            SELECT *
            FROM messages m
            WHERE (CAST(:type AS text) IS NULL OR m.type = CAST(:type AS text))
              AND m.conversation_id = :conversationId
              AND (
                CAST (:before AS timestamptz) IS NULL
                OR m.created_at < CAST (:before AS timestamptz)
              )
              AND EXISTS (
                SELECT 1 FROM message_media mm WHERE mm.message_id = m.id
              )
            ORDER BY m.created_at DESC
            LIMIT 20
            """, nativeQuery = true)
    public List<Message> findMediaMessages(
            String type, UUID conversationId, Instant before);
}
