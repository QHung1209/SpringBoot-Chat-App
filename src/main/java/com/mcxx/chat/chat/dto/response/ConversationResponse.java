package com.mcxx.chat.chat.dto.response;

import com.mcxx.chat.chat.domain.Conversation;
import java.time.Instant;
import java.util.UUID;
import com.mcxx.chat.chat.domain.ConversationType;
import lombok.Data;

@Data
public class ConversationResponse {
  private UUID id;
  private ConversationType type;
  private String name;
  private String avatarUrl;
  private Instant updatedAt;
  private String content;
  private UUID senderId;
  private UUID lastMessageId;

  public static ConversationResponse from(Conversation conversation) {
    ConversationResponse response = new ConversationResponse();
    response.setId(conversation.getId());
    response.setType(conversation.getType());
    response.setName(conversation.getName());
    response.setAvatarUrl(conversation.getAvatarUrl());
    response.setUpdatedAt(conversation.getUpdatedAt());
    response.setLastMessageId(conversation.getLastMessageId());
    return response;
  }
}
