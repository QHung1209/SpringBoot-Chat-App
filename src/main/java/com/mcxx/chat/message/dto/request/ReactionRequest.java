package com.mcxx.chat.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReactionRequest {
  @NotBlank
  private String reaction;
}
