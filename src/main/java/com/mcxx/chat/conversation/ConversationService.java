package com.mcxx.chat.conversation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.mcxx.chat.common.exception.NotFoundException;
import com.mcxx.chat.common.util.PairKeyGenerator;
import com.mcxx.chat.conversation.constants.ConversationRole;
import com.mcxx.chat.conversation.dto.request.CreateGroupRequest;
import com.mcxx.chat.conversation.dto.request.UpdateGroupRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationService {

  private final ConversationRepository conversationRepository;
  private final ConversationMemberRepository conversationMemberRepository;
  private final PairKeyGenerator pairKeyGenerator;

  @Transactional
  public Conversation createDirectConversation(UUID user1, UUID user2) {
    String pairKey = pairKeyGenerator.generate(user1, user2);

    Optional<Conversation> existing = conversationRepository.findByPairKey(pairKey);
    if (existing.isPresent()) {
      return existing.get();
    }

    try {
      Conversation conversation =
          conversationRepository.save(new Conversation(null, "DIRECT", null, pairKey, null, user1));

      conversationMemberRepository.saveAll(List.of(
          new ConversationMember(conversation.getId(), "", user1, ConversationRole.ADMIN, null,
              null),
          new ConversationMember(conversation.getId(), "", user2, ConversationRole.MEMBER, null,
              null)));

      return conversation;
    } catch (DataIntegrityViolationException e) {
      return conversationRepository.findByPairKey(pairKey).orElseThrow(() -> e);
    }
  }

  public Conversation createGroupConversation(UUID meId, CreateGroupRequest request) {
    Conversation conv = conversationRepository
        .save(new Conversation(null, "GROUP", request.getName(), null, null, meId));
    List<ConversationMember> members = request.getMemberIds().stream()
        .map(
            id -> new ConversationMember(conv.getId(), "", id, ConversationRole.MEMBER, null, null))
        .collect(Collectors.toCollection(ArrayList::new));

    members.add(new ConversationMember(conv.getId(), "", meId, ConversationRole.ADMIN, null, null));
    conversationMemberRepository.saveAll(members);

    return conv;
  }

  public List<Conversation> getConversations(UUID userId, Instant updatedTime) {
    return conversationRepository.getConversations(userId, updatedTime);
  }

  public Conversation detail(UUID conversationId) {
    return conversationRepository.findById(conversationId)
        .orElseThrow(() -> new NotFoundException(Conversation.class, conversationId));
  }

  public void updateGroupInfo(UUID conversationId, UpdateGroupRequest request) {
    Conversation conversation = conversationRepository.findById(conversationId)
        .orElseThrow(() -> new NotFoundException(Conversation.class, conversationId));

    conversation.setName(request.getName());
    conversation.setAvatarUrl(request.getAvatarUrl());
    conversationRepository.save(conversation);
  }

}
