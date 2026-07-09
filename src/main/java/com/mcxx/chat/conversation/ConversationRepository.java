package com.mcxx.chat.conversation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

  Optional<Conversation> findByPairKey(String pairKey);

  @Query(value = """
      SELECT co
      FROM conversations co
      LEFT JOIN conversation_members cm ON co.id = cm.conversation_id
      WHERE cm.user_id = :userId
        AND (:updatedTime IS NULL OR co.updated_at < :updatedTime)
      ORDER BY co.updated_at DESC
      LIMIT 10
      """, nativeQuery = true)
  List<Conversation> getConversations(UUID userId, Instant updatedTime);


}
