package com.mcxx.chat.chat.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.databind.JsonNode;
import com.mcxx.chat.common.util.BaseEntity;
import com.mcxx.chat.media.domain.Media;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "messages")
@Getter
@Setter
public class Message extends BaseEntity {

  private UUID conversationId;
  private UUID senderId;
  @Enumerated(EnumType.STRING)
  private MessageType type;
  private String content;
  @JdbcTypeCode(SqlTypes.JSON)
  private JsonNode metadata;
  private UUID replyToMessageId;
  @Column(nullable = false)
  private Boolean isPinned = false;
  private Instant deletedAt;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "message_media", joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "media_id"))
  @OrderColumn(name = "position")
  private List<Media> medias = new ArrayList<>();
}
