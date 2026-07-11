package com.mcxx.chat.message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.mcxx.chat.message.dto.response.ReactionResponse;
import com.mcxx.chat.message.projection.ReactionSummaryProjection;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageReactionService {
  private final MessageReactionRepository messageReactionRepository;

  public List<ReactionResponse> getReactions(List<UUID> messageIds) {
    List<ReactionSummaryProjection> projections =
        messageReactionRepository.findByMessageIds(messageIds);
    return projections.stream().map(ReactionResponse::from).toList();
  }

  public List<ReactionResponse> react(UUID messageId, UUID userId, String reaction) {
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

    return getReactions(List.of(messageId));
    // TODO optimize by redis cache for highly frequently accessed reaction
  }

}
