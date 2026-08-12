package com.mcxx.chat.message.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.mcxx.chat.message.domain.Message;
import lombok.Data;

@Data
public class MessageResponse {
  private UUID id;
  private UUID conversationId;
  private UUID senderId;
  private String type;
  private String content;
  private UUID replyToMessageId;
  private MessageResponse replyToMessage;
  private UUID mediaId;
  private Boolean isPinned;
  private Instant createdAt;
  private Instant updatedAt;
  private Instant deletedAt;
  private List<ReactionResponse> reactions;

  public static MessageResponse from(Message message) {
    return from(message, null);
  }

  public static MessageResponse from(Message message, Message replyMessage) {
    MessageResponse resp = new MessageResponse();
    resp.setId(message.getId());
    resp.setConversationId(message.getConversationId());
    resp.setSenderId(message.getSenderId());
    resp.setType(message.getType());
    resp.setContent(message.getDeletedAt() != null ? null : message.getContent());
    resp.setReplyToMessageId(message.getReplyToMessageId());
    resp.setMediaId(message.getMediaId());
    resp.setIsPinned(message.getIsPinned());
    resp.setCreatedAt(message.getCreatedAt());
    resp.setUpdatedAt(message.getUpdatedAt());
    resp.setDeletedAt(message.getDeletedAt());

    if (replyMessage != null) {
      resp.setReplyToMessage(from(replyMessage, null));
    }
    return resp;
  }

  public static MessageResponse from(com.mcxx.chat.message.repository.MessageWithReplyProjection p) {
    MessageResponse resp = new MessageResponse();
    resp.setId(p.getId());
    resp.setConversationId(p.getConversationId());
    resp.setSenderId(p.getSenderId());
    resp.setType(p.getType());
    resp.setContent(p.getDeletedAt() != null ? null : p.getContent());
    resp.setReplyToMessageId(p.getReplyToMessageId());
    resp.setMediaId(p.getMediaId());
    resp.setIsPinned(p.getIsPinned());
    resp.setCreatedAt(p.getCreatedAt());
    resp.setUpdatedAt(p.getUpdatedAt());
    resp.setDeletedAt(p.getDeletedAt());

    if (p.getReplyId() != null) {
      MessageResponse reply = new MessageResponse();
      reply.setId(p.getReplyId());
      reply.setConversationId(p.getConversationId());
      reply.setSenderId(p.getReplySenderId());
      reply.setType(p.getReplyType());
      reply.setContent(p.getReplyDeletedAt() != null ? null : p.getReplyContent());
      reply.setMediaId(p.getReplyMediaId());
      reply.setCreatedAt(p.getReplyCreatedAt());
      reply.setDeletedAt(p.getReplyDeletedAt());
      resp.setReplyToMessage(reply);
    }
    return resp;
  }
}

