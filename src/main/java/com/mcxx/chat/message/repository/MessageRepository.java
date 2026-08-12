package com.mcxx.chat.message.repository;

import com.mcxx.chat.message.domain.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query(value = """
            SELECT id, content, conversation_id, sender_id, type, metadata, reply_to_message_id, media_id,
            is_pinned, deleted_at,created_at, updated_at
            FROM messages
            WHERE conversation_id = :conversationId
            AND (cast(:updatedAt as timestamp) IS NULL OR updated_at < cast(:updatedAt as timestamp))
            ORDER BY updated_at DESC
            LIMIT 15
            """,
            nativeQuery = true)
    public List<Message> findByConversationIdOrderByUpdatedAtDesc(UUID conversationId,
            Instant updatedAt);
}
