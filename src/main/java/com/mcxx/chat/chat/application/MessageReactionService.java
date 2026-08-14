package com.mcxx.chat.chat.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mcxx.chat.chat.domain.Message;
import com.mcxx.chat.chat.domain.MessageReaction;
import com.mcxx.chat.chat.dto.response.ReactionResponse;
import com.mcxx.chat.chat.event.MessageReactionEvent;
import com.mcxx.chat.chat.repository.MessageReactionRepository;
import com.mcxx.chat.chat.repository.MessageRepository;
import com.mcxx.chat.chat.repository.ReactionSummaryProjection;
import com.mcxx.chat.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageReactionService {
  private final MessageReactionRepository messageReactionRepository;
  private final MessageRepository messageRepository;
  private final ApplicationEventPublisher eventPublisher;

  public List<ReactionResponse> getReactions(List<UUID> messageIds, UUID userId) {
    if (messageIds.isEmpty()) {
      return List.of();
    }

    List<ReactionSummaryProjection> projections =
        messageReactionRepository.findByMessageIds(messageIds, userId);
    return projections.stream().map(ReactionResponse::from).toList();
  }

  @Transactional
  public List<ReactionResponse> react(UUID messageId, UUID userId, String reaction) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new BadRequestException("Invalid message"));

    Optional<MessageReaction> existing =
        messageReactionRepository.findByMessageIdAndUserId(messageId, userId);

    if (existing.isPresent()) {
      MessageReaction current = existing.get();

      if (current.getReaction().equals(reaction)) {
        messageReactionRepository.delete(current);
      } else {
        current.setReaction(reaction);
        messageReactionRepository.save(current);
      }
    } else {
      MessageReaction entity = new MessageReaction();
      entity.setMessageId(messageId);
      entity.setUserId(userId);
      entity.setReaction(reaction);
      messageReactionRepository.save(entity);
    }

    List<ReactionResponse> reactions = getReactions(List.of(messageId), userId);
    eventPublisher.publishEvent(
        new MessageReactionEvent(message.getConversationId(), messageId, userId, reactions, Instant.now()));

    return reactions;
  }
}
