package com.mcxx.chat.conversation.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberIdRequest {
  @NotNull(message = "Member id is required")
  private UUID memberId;
}
