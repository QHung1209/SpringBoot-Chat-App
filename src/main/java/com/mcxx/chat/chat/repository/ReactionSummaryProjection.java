package com.mcxx.chat.chat.repository;

import java.util.UUID;

public interface ReactionSummaryProjection {
  UUID getMessageId();

  String getReaction();

  Long getCount();

  Boolean getReacted();
}
