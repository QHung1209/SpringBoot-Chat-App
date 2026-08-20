package com.mcxx.chat.chat.dto.request;

import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;
import com.mcxx.chat.chat.domain.MessageType;

import lombok.Data;

@Data
public class CreateMessage {
  private UUID conversationId;
  private UUID senderId;
  private MessageType type;
  private String content;
  private UUID replyToMessageId;
  private JsonNode metadata;
  private List<UUID> mediaIds;
}
