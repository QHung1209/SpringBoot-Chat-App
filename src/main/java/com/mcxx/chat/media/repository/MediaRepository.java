package com.mcxx.chat.media.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mcxx.chat.media.domain.Media;

public interface MediaRepository extends JpaRepository<Media, UUID> {
  Optional<Media> findById(UUID id);

  List<Media> findByIdIn(List<UUID> ids);

  /**
   * Batch load message_id → media_id mappings for a list of message IDs, preserving position order.
   */
  @Query(value = """
      SELECT mm.message_id AS messageId, mm.media_id AS mediaId
      FROM message_media mm
      WHERE mm.message_id IN (:messageIds)
      ORDER BY mm.message_id, mm.position
      """, nativeQuery = true)
  List<MessageMediaProjection> findMessageMediaMappings(List<UUID> messageIds);
}
