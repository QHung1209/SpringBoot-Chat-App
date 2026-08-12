package com.mcxx.chat.userrelation.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TargetUserRequest {
  @NotNull(message = "Target id is required")
  private UUID targetId;
}
