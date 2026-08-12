package com.mcxx.chat.message.repository;

import java.time.Instant;
import java.util.UUID;

public interface MessageWithReplyProjection {

    UUID getId();

    String getContent();

    UUID getConversationId();

    UUID getSenderId();

    String getType();

    String getMetadata();

    UUID getReplyToMessageId();

    UUID getMediaId();

    Boolean getIsPinned();

    Instant getDeletedAt();

    Instant getCreatedAt();

    Instant getUpdatedAt();

    UUID getReplyId();

    String getReplyContent();

    UUID getReplySenderId();

    String getReplyType();

    UUID getReplyMediaId();

    Instant getReplyDeletedAt();

    Instant getReplyCreatedAt();
}
