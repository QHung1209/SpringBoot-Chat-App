package com.mcxx.chat.chat.repository.projection;

import java.time.Instant;
import java.util.UUID;

import com.mcxx.chat.chat.domain.MessageType;

public interface MessageWithReplyProjection {

    UUID getId();

    String getContent();

    UUID getConversationId();

    UUID getSenderId();

    MessageType getType();

    String getMetadata();

    Boolean getIsPinned();

    Instant getDeletedAt();

    Instant getCreatedAt();

    Instant getUpdatedAt();

    UUID getReplyId();

    String getReplyContent();

    UUID getReplySenderId();

    MessageType getReplyType();

    Instant getReplyDeletedAt();

    Instant getReplyCreatedAt();
}
