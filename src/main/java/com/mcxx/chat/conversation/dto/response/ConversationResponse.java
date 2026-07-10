package com.mcxx.chat.conversation.dto.response;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class ConversationResponse {
  private UUID id;
  private String type;
  private String name;
  private String avatarUrl;
  private Instant updatedAt;
  private String content;
  private UUID senderId;
  private UUID lastMessageId;
}
