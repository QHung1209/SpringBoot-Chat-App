package com.mcxx.chat.presence.event;

import java.util.UUID;

public record UserPresenceWsEvent(UUID userId, boolean online, Long lastSeen) {
}
