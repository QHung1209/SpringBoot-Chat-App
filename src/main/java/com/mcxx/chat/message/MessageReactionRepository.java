package com.mcxx.chat.message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mcxx.chat.message.projection.ReactionSummaryProjection;

public interface MessageReactionRepository extends JpaRepository<MessageReaction, UUID> {
  @Query(value = """
      SELECT
        reaction, message_id,
        COUNT(*) as count,
        BOOL_OR(user_id = :userId) AS reacted
      FROM message_reactions
      WHERE message_id IN :messageIds
      GROUP BY reaction, message_id
        """, nativeQuery = true)
  List<ReactionSummaryProjection> findByMessageIds(List<UUID> messageIds);

  void deleteByMessageIdAndUserId(UUID messageId, UUID userId);

  boolean existsByMessageIdAndUserIdAndReaction(UUID messageId, UUID userId, String reaction);

  Optional<MessageReaction> findByMessageIdAndUserId(UUID messageId, UUID userId);
}
