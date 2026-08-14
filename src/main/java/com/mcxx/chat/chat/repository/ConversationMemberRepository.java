package com.mcxx.chat.chat.repository;

import com.mcxx.chat.chat.domain.ConversationMember;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.mcxx.chat.chat.domain.ConversationRole;

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

    @Query(value = """
            SELECT user_id
            FROM conversation_members
            WHERE conversation_id = :conversationId
            """, nativeQuery = true)
    List<UUID> findUserIdsByConversationId(UUID conversationId);

    Optional<ConversationMember> findByConversationIdAndUserId(UUID conversationId, UUID userId);

    Long countByConversationIdAndRole(UUID conversationId, ConversationRole role);

    @Query(value = """
            SELECT u.id AS id,
                   u.first_name AS "firstName",
                   u.last_name AS "lastName",
                   u.avatar_url AS "avatarUrl",
                   cm.role AS role,
                   cm.created_at AS "createdAt",
                   cm.last_read_message_id AS "lastSeenMessageId"
            FROM conversation_members cm
            JOIN users u ON u.id = cm.user_id
            WHERE cm.conversation_id = :conversationId
            AND (cast(:createdAt as timestamp) IS NULL OR cm.created_at > cast(:createdAt as timestamp))
            ORDER BY cm.created_at ASC
            LIMIT 30
            """,
            nativeQuery = true)
    List<MemberProfileProjection> findAllByConversationIdOrderByCreatedAtAsc(UUID conversationId,
            Instant createdAt);

    Boolean existsByConversationIdAndUserId(UUID conversationId, UUID userId);

    @Modifying
    @Query(value = """
            UPDATE conversation_members
            SET last_read_message_id = :lastReadMessageId
            WHERE conversation_id = :conversationId AND user_id = :userId
            """, nativeQuery = true)
    void seenMessage(UUID conversationId, UUID userId, UUID lastReadMessageId);
}
