package com.mcxx.chat.conversation.projection;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class ConversationMessageProjection {
  private UUID id;
  private String type;
  private String name;
  private String avatarUrl;
  private Instant updatedAt;
  private String content;
  private UUID senderId;
  private UUID lastMessageId;
}
