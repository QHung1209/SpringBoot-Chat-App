package com.mcxx.chat.chat.dto.request;

import java.util.UUID;
import com.mcxx.chat.chat.validation.ValidSendMessage;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
@ValidSendMessage
public class SendMessageRequest {
  private UUID conversationId;
  private UUID receiverId;
  private String content;
  @NotBlank
  private String type;
  private UUID replyToMessageId;
  private UUID mediaId;
}
