package com.mcxx.chat.chat.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.mcxx.chat.chat.dto.response.ReactionResponse;

public record MessageReactionEvent(
    UUID conversationId,
    UUID messageId,
    UUID userId,
    List<ReactionResponse> reactions,
    Instant timestamp
) {}
