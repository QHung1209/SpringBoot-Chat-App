package com.mcxx.chat.chat.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import com.mcxx.chat.chat.application.ChatRealtimeService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageListener {
  private final ChatRealtimeService chatRealtimeService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onMessageCreated(MessageCreatedEvent event) {
    chatRealtimeService.publishMessage(event.message());
  }

  @EventListener
  public void onMemberAdded(MemberAddedEvent event) {
    chatRealtimeService.publishMemberAdded(event);
  }

  @EventListener
  public void onMemberLeft(MemberLeftEvent event) {
    chatRealtimeService.publishMemberLeft(event);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onMessageDeleted(MessageDeletedEvent event) {
    chatRealtimeService.publishDeleteMessage(event);
  }

  @EventListener
  public void onTyping(TypingEvent event) {
    chatRealtimeService.publishTyping(event);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onMessageReactionChanged(MessageReactionEvent event) {
    chatRealtimeService.publishReaction(event);
  }

  @EventListener
  public void onMessageSeen(MessageSeenEvent event) {
    chatRealtimeService.publishSeen(event);
  }
}
