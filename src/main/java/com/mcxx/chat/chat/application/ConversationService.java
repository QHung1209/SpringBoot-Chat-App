package com.mcxx.chat.chat.application;

import com.mcxx.chat.chat.repository.ConversationMemberRepository;
import com.mcxx.chat.chat.repository.ConversationRepository;
import com.mcxx.chat.chat.domain.ConversationMember;
import com.mcxx.chat.chat.domain.Conversation;
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
import com.mcxx.chat.chat.domain.ConversationRole;
import com.mcxx.chat.chat.domain.ConversationType;
import com.mcxx.chat.chat.dto.request.CreateGroupRequest;
import com.mcxx.chat.chat.dto.request.UpdateGroupRequest;
import com.mcxx.chat.chat.dto.response.ConversationResponse;
import com.mcxx.chat.chat.repository.ConversationMessageProjection;
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
          conversationRepository
              .save(new Conversation(null, ConversationType.DIRECT, null, pairKey, null, user1));

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
    Conversation conversation = new Conversation();
    conversation.setType(ConversationType.GROUP);
    conversation.setName(request.getName());
    conversation.setCreatedBy(meId);
    Conversation conv = conversationRepository.save(conversation);
    List<ConversationMember> members = request.getMemberIds().stream()
        .map(
            id -> new ConversationMember(conv.getId(), "", id, ConversationRole.MEMBER, null, null))
        .collect(Collectors.toCollection(ArrayList::new));

    members.add(new ConversationMember(conv.getId(), "", meId, ConversationRole.ADMIN, null, null));
    conversationMemberRepository.saveAll(members);

    return conv;
  }

  public List<ConversationResponse> getConversations(UUID userId, Instant updatedTime) {
    List<ConversationMessageProjection> list =
        conversationRepository.getConversations(userId, updatedTime);

    return list.stream().map(proj -> {
      ConversationResponse resp = new ConversationResponse();
      resp.setId(proj.getId());
      resp.setType(ConversationType.valueOf(proj.getType()));
      resp.setName(proj.getName());
      resp.setAvatarUrl(proj.getAvatarUrl());
      resp.setUpdatedAt(proj.getUpdatedAt());
      resp.setContent(proj.getContent());
      resp.setSenderId(proj.getSenderId());
      resp.setLastMessageId(proj.getLastMessageId());
      return resp;
    }).toList();
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

  public void updateLastMessage(UUID conversationId, UUID messageId, Instant at) {
    Conversation conv = conversationRepository.findById(conversationId)
        .orElseThrow(() -> new NotFoundException(Conversation.class, conversationId));
    conv.setLastMessageId(messageId);
    conv.setUpdatedAt(at);
    conversationRepository.save(conv);
  }

}
