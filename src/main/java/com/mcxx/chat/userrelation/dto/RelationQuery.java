package com.mcxx.chat.userrelation.dto;

import java.util.UUID;
import com.mcxx.chat.userrelation.RelationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelationQuery {
  private UUID relationId;
  private String search;

  @NotNull(message = "Status is required")
  private RelationStatus status;
}
