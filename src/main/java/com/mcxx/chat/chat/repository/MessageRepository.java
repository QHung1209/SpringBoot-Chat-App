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
                m.media_id AS mediaId,
                m.is_pinned AS isPinned,
                m.deleted_at AS deletedAt,
                m.created_at AS createdAt,
                m.updated_at AS updatedAt,

                rm.id AS replyId,
                rm.content AS replyContent,
                rm.sender_id AS replySenderId,
                rm.type AS replyType,
                rm.media_id AS replyMediaId,
                rm.deleted_at AS replyDeletedAt,
                rm.created_at AS replyCreatedAt

            FROM messages m
            LEFT JOIN messages rm
                ON rm.id = m.reply_to_message_id
            WHERE m.conversation_id = :conversationId
              AND (
                  CAST(:before AS timestamp) IS NULL
                  OR m.updated_at < CAST(:before AS timestamp)
              )
              AND (
                  CAST(:after AS timestamp) IS NULL
                  OR m.updated_at > CAST(:after AS timestamp)
              )
            ORDER BY m.updated_at DESC
            LIMIT 15
                        """, nativeQuery = true)
    public List<MessageWithReplyProjection> findByConversationIdOrderByUpdatedAtDesc(
            UUID conversationId, Instant before, Instant after);
}
