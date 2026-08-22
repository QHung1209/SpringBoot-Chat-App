package com.mcxx.chat.chat.repository.projection;

import java.time.Instant;
import java.util.UUID;
import com.mcxx.chat.chat.domain.ConversationRole;

public interface MemberProfileProjection {
  UUID getId();

  String getFirstName();

  String getLastName();

  String getAvatarUrl();

  ConversationRole getRole();

  Instant getCreatedAt();

  UUID getLastSeenMessageId();
}
