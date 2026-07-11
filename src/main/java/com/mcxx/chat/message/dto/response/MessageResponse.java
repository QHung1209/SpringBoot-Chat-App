package com.mcxx.chat.message.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.mcxx.chat.message.Message;
import lombok.Data;

@Data
public class MessageResponse {
  private UUID id;
  private UUID conversationId;
  private UUID senderId;
  private String type;
  private String content;
  private UUID replyToMessageId;
  private UUID mediaId;
  private Boolean isPinned;
  private Instant createdAt;
  private Instant updatedAt;
  private List<ReactionResponse> reactions;

  public static MessageResponse from(Message message) {
    MessageResponse resp = new MessageResponse();
    resp.setId(message.getId());
    resp.setConversationId(message.getConversationId());
    resp.setSenderId(message.getSenderId());
    resp.setType(message.getType());
    resp.setContent(message.getContent());
    resp.setReplyToMessageId(message.getReplyToMessageId());
    resp.setMediaId(message.getMediaId());
    resp.setIsPinned(message.getIsPinned());
    resp.setCreatedAt(message.getCreatedAt());
    resp.setUpdatedAt(message.getUpdatedAt());
    return resp;
  }
}
