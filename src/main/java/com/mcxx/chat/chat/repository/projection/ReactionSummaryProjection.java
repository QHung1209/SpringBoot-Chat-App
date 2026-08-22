package com.mcxx.chat.chat.repository.projection;

import java.util.UUID;

public interface ReactionSummaryProjection {
  UUID getMessageId();

  String getReaction();

  Long getCount();

  Boolean getReacted();
}
