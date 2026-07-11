package com.mcxx.chat.message.projection;

import java.util.UUID;

public interface ReactionSummaryProjection {
  UUID getMessageId();

  String getReaction();

  Long getCount();

  Boolean getReacted();
}
