package com.mcxx.chat.conversation.repository;

import java.time.Instant;
import java.util.UUID;

public interface ConversationMessageProjection {
  UUID getId();

  String getType();

  String getName();

  String getAvatarUrl();

  Instant getUpdatedAt();

  String getContent();

  UUID getSenderId();

  UUID getLastMessageId();
}
