package com.mcxx.chat.presence.dto.response;

public record PresenceResponse(
    boolean online,
    Long lastSeen
) {}
