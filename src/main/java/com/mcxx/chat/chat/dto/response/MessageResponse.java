package com.mcxx.chat.chat.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcxx.chat.chat.domain.Message;
import com.mcxx.chat.chat.domain.MessageType;
import com.mcxx.chat.chat.repository.projection.MessageWithReplyProjection;
import com.mcxx.chat.media.dto.response.MediaResponse;
import lombok.Data;

@Data
public class MessageResponse {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private UUID id;
  private UUID conversationId;
  private UUID senderId;
  private MessageType type;
  private String content;
  private MessageResponse replyToMessage;
  private List<MediaResponse> medias;
  private Boolean isPinned;
  private JsonNode metadata;
  private Instant createdAt;
  private Instant updatedAt;
  private Instant deletedAt;
  private List<ReactionResponse> reactions;

  public static MessageResponse from(Message message) {
    return from(message, null, List.of());
  }

  public static MessageResponse from(Message message, Message replyMessage) {
    return from(message, replyMessage, List.of());
  }

  public static MessageResponse from(Message message, Message replyMessage,
      List<MediaResponse> medias) {
    return from(message, replyMessage, medias, List.of());
  }

  public static MessageResponse from(Message message, Message replyMessage,
      List<MediaResponse> medias, List<MediaResponse> replyMedias) {
    MessageResponse resp = new MessageResponse();
    resp.setId(message.getId());
    resp.setConversationId(message.getConversationId());
    resp.setSenderId(message.getSenderId());
    resp.setType(message.getType());
    resp.setContent(message.getDeletedAt() != null ? null : message.getContent());
    resp.setMedias(medias);
    resp.setIsPinned(message.getIsPinned());
    resp.setMetadata(message.getMetadata());
    resp.setCreatedAt(message.getCreatedAt());
    resp.setUpdatedAt(message.getUpdatedAt());
    resp.setDeletedAt(message.getDeletedAt());

    if (replyMessage != null) {
      resp.setReplyToMessage(from(replyMessage, null, replyMedias));
    }
    return resp;
  }

  public static MessageResponse from(MessageWithReplyProjection p) {
    return from(p, Map.of());
  }

  public static MessageResponse from(MessageWithReplyProjection p,
      Map<UUID, List<MediaResponse>> mediasByMessage) {
    MessageResponse resp = new MessageResponse();
    resp.setId(p.getId());
    resp.setConversationId(p.getConversationId());
    resp.setSenderId(p.getSenderId());
    resp.setType(p.getType());
    resp.setContent(p.getDeletedAt() != null ? null : p.getContent());
    resp.setMedias(mediasByMessage.getOrDefault(p.getId(), List.of()));
    resp.setIsPinned(p.getIsPinned());
    resp.setCreatedAt(p.getCreatedAt());
    resp.setUpdatedAt(p.getUpdatedAt());
    resp.setDeletedAt(p.getDeletedAt());

    if (p.getMetadata() != null) {
      try {
        resp.setMetadata(OBJECT_MAPPER.readTree(p.getMetadata()));
      } catch (Exception e) {
        resp.setMetadata(null);
      }
    }

    if (p.getReplyId() != null) {
      MessageResponse reply = new MessageResponse();
      reply.setId(p.getReplyId());
      reply.setConversationId(p.getConversationId());
      reply.setSenderId(p.getReplySenderId());
      reply.setType(p.getReplyType());
      reply.setContent(p.getReplyDeletedAt() != null ? null : p.getReplyContent());
      reply.setMedias(mediasByMessage.getOrDefault(p.getReplyId(), List.of()));
      reply.setCreatedAt(p.getReplyCreatedAt());
      reply.setDeletedAt(p.getReplyDeletedAt());
      resp.setReplyToMessage(reply);
    }
    return resp;
  }
}

