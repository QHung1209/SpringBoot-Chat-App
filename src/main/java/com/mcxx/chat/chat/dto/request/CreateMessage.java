package com.mcxx.chat.chat.dto.request;

import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class CreateMessage {
  private UUID conversationId;
  private UUID senderId;
  private String type;
  private String content;
  private UUID replyToMessageId;
  private JsonNode metadata;
  private UUID mediaId;
}
