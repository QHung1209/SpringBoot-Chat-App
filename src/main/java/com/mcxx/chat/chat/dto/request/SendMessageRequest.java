package com.mcxx.chat.chat.dto.request;

import java.util.List;
import java.util.UUID;

import com.mcxx.chat.chat.domain.MessageType;
import com.mcxx.chat.chat.validation.ValidSendMessage;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
@ValidSendMessage
public class SendMessageRequest {
  private UUID conversationId;
  private UUID receiverId;
  private String content;
  @NotNull
  private MessageType type;
  private UUID replyToMessageId;
  private List<UUID> mediaIds;
}
