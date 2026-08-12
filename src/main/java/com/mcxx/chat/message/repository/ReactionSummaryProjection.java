package com.mcxx.chat.message.repository;

import java.util.UUID;

public interface ReactionSummaryProjection {
  UUID getMessageId();

  String getReaction();

  Long getCount();

  Boolean getReacted();
}
