package com.mcxx.chat.media.repository;

import java.util.UUID;

public interface MessageMediaProjection {
  UUID getMessageId();
  UUID getMediaId();
}
