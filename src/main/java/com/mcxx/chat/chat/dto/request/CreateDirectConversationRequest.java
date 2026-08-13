package com.mcxx.chat.chat.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDirectConversationRequest {
  @NotNull(message = "Other user id is required")
  private UUID otherUserId;
}
