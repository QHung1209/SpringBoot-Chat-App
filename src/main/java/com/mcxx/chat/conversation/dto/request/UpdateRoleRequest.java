package com.mcxx.chat.conversation.dto.request;

import java.util.UUID;
import com.mcxx.chat.conversation.constants.ConversationRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleRequest {
  private UUID memberId;
  private ConversationRole role;
}
