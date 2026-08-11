package com.mcxx.chat.conversation.dto.request;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import jakarta.validation.constraints.NotEmpty;
import lombok.Setter;

@Getter
@Setter
public class AddMembersRequest {
  @NotEmpty(message = "Member IDs are required")
  private List<UUID> memberIds;
}
