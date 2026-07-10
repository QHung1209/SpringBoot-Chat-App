package com.mcxx.chat.message;

import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.databind.JsonNode;
import com.mcxx.chat.common.util.BaseEntity;
import jakarta.persistence.Entity;
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
  private String type;
  private String content;
  @JdbcTypeCode(SqlTypes.JSON)
  private JsonNode metadata;
  private UUID replyToMessageId;
  private UUID mediaId;
  private Boolean isPinned;
  private Instant deletedAt;

}
