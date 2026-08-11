package com.mcxx.chat.conversation.dto.request;

import java.util.UUID;
import com.mcxx.chat.conversation.constants.ConversationRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleRequest {
  @NotNull
  private UUID memberId;
  @NotNull
  private ConversationRole role;
}
