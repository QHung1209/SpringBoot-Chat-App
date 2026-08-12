package com.mcxx.chat.message.event;

import java.util.UUID;
import com.mcxx.chat.message.domain.Message;

public record MessageCreatedEvent(UUID conversationId, Message message) {

}
