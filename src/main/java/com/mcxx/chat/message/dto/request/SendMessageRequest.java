package com.mcxx.chat.message.dto.request;

import java.util.UUID;
import lombok.Data;
import com.mcxx.chat.message.validation.ValidSendMessage;

@Data
@ValidSendMessage
public class SendMessageRequest {
  private UUID conversationId;
  private UUID receiverId;
  private String content;
  private String type;
  private UUID replyToMessageId;
  private UUID mediaId;
}
