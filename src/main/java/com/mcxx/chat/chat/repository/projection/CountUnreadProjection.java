package com.mcxx.chat.chat.repository.projection;

import java.util.UUID;

public interface CountUnreadProjection {
  UUID getConversationId();

  Long getCount();
}
