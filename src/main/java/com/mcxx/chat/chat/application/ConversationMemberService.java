package com.mcxx.chat.chat.application;

import com.mcxx.chat.chat.repository.ConversationMemberRepository;
import com.mcxx.chat.chat.repository.ConversationRepository;
import com.mcxx.chat.chat.repository.MemberProfileProjection;
import com.mcxx.chat.chat.domain.ConversationMember;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mcxx.chat.common.exception.ForbiddenException;
import com.mcxx.chat.common.exception.NotFoundException;
import com.mcxx.chat.chat.domain.ConversationRole;
import com.mcxx.chat.chat.event.MemberAddedEvent;
import com.mcxx.chat.chat.event.MemberLeftEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationMemberService {

  private final ConversationMemberRepository conversationMemberRepository;
  private final ConversationRepository conversationRepository;
  private final ConversationMemberCacheService memberCacheService;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Transactional(readOnly = true)
  public List<MemberProfileProjection> getMembers(UUID conversationId, Instant createdAt) {
    return conversationMemberRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId,
        createdAt);
  }

  @Cacheable(cacheNames = "conversation-members", key = "#conversationId")
  public List<UUID> getMemberIds(UUID conversationId) {
    return conversationMemberRepository.findUserIdsByConversationId(conversationId);
  }

  public void addMembers(UUID meId, UUID conversationId, List<UUID> memberIds) {
    List<ConversationMember> members =
        memberIds.stream().map(id -> new ConversationMember(conversationId, id, meId)).toList();
    conversationMemberRepository.saveAll(members);

    evictMemberIdsCache(conversationId);
    memberCacheService.addMembers(conversationId, memberIds);

    List<UUID> addedMemberIds = memberIds.stream().filter(id -> !id.equals(meId)).toList();
    addedMemberIds.forEach(id -> applicationEventPublisher
        .publishEvent(new MemberAddedEvent(conversationId, meId, id)));
  }

  private void deleteMemberInternal(UUID conversationId, UUID userId) {
    conversationMemberRepository.removeMember(conversationId, userId);
    evictMemberIdsCache(conversationId);
    memberCacheService.removeMember(conversationId, userId);
  }

  public void removeMember(UUID meId, UUID conversationId, UUID userId) {
    if (meId.equals(userId)) {
      leaveConversation(meId, conversationId);
      return;
    }

    deleteMemberInternal(conversationId, userId);
    applicationEventPublisher.publishEvent(new MemberLeftEvent(conversationId, meId, userId));
  }

  public void updateRole(UUID conversationId, UUID userId, ConversationRole role) {
    conversationMemberRepository.updateRole(conversationId, userId, role);
  }

  public void leaveConversation(UUID meId, UUID conversationId) {
    ConversationMember member =
        conversationMemberRepository.findByConversationIdAndUserId(conversationId, meId)
            .orElseThrow(() -> new NotFoundException(ConversationMember.class, meId));
    List<ConversationMember> members =
        conversationMemberRepository.findAllByConversationId(conversationId);

    if (members.size() == 1) {
      deleteMemberInternal(conversationId, meId);
      conversationRepository.deleteById(conversationId);
      memberCacheService.evict(conversationId);
      applicationEventPublisher.publishEvent(new MemberLeftEvent(conversationId, meId, meId));
      return;
    }

    if (member.getRole() == ConversationRole.ADMIN) {
      Long countAdmin = conversationMemberRepository.countByConversationIdAndRole(conversationId,
          ConversationRole.ADMIN);
      if (countAdmin == 1) {
        List<ConversationMember> otherMembers =
            members.stream().filter(m -> !m.getUserId().equals(meId)).toList();
        if (!otherMembers.isEmpty()) {
          ConversationMember newAdmin = otherMembers.get(0);
          updateRole(conversationId, newAdmin.getUserId(), ConversationRole.ADMIN);
        }
      }
    }

    deleteMemberInternal(conversationId, meId);
    applicationEventPublisher.publishEvent(new MemberLeftEvent(conversationId, meId, meId));
  }

  public void seenMessage(UUID conversationId, UUID userId, UUID lastReadMessageId) {
    conversationMemberRepository.seenMessage(conversationId, userId, lastReadMessageId);
  }

  @Transactional(readOnly = true)
  public Boolean isMember(UUID conversationId, UUID userId) {
    Boolean cached = memberCacheService.isMember(conversationId, userId);
    if (cached != null) {
      return cached;
    }

    List<UUID> memberIds = conversationMemberRepository.findUserIdsByConversationId(conversationId);
    memberCacheService.populate(conversationId, memberIds);

    return memberIds.contains(userId);
  }

  @CacheEvict(cacheNames = "conversation-members", key = "#conversationId")
  public void evictMemberIdsCache(UUID conversationId) {}

}
