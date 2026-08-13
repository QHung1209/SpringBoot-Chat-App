package com.mcxx.chat.chat.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.mcxx.chat.chat.application.ChatRealtimeService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageListener {
  private final ChatRealtimeService chatRealtimeService;

  @EventListener
  public void onMessageCreated(MessageCreatedEvent event) {
    chatRealtimeService.publishMessage(event.message());
  }
}
