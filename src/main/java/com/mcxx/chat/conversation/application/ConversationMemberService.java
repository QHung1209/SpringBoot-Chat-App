package com.mcxx.chat.conversation.application;

import com.mcxx.chat.conversation.repository.ConversationMemberRepository;
import com.mcxx.chat.conversation.repository.ConversationRepository;
import com.mcxx.chat.conversation.domain.ConversationMember;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mcxx.chat.common.exception.NotFoundException;
import com.mcxx.chat.conversation.domain.ConversationRole;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationMemberService {

  private final ConversationMemberRepository conversationMemberRepository;
  private final ConversationRepository conversationRepository;
  private final ConversationMemberCacheService memberCacheService;

  @Transactional(readOnly = true)
  public List<ConversationMember> getMembers(UUID conversationId, Instant createdAt) {
    return conversationMemberRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId,
        createdAt);
  }

  @Cacheable(cacheNames = "conversation-members", key = "#conversationId")
  public List<UUID> getMemberIds(UUID conversationId) {
    return conversationMemberRepository.findUserIdsByConversationId(conversationId);
  }

  public void addMembers(UUID conversationId, List<UUID> memberIds) {
    List<ConversationMember> members =
        memberIds.stream().map(id -> new ConversationMember(conversationId, id)).toList();
    conversationMemberRepository.saveAll(members);

    evictMemberIdsCache(conversationId);
    memberCacheService.addMembers(conversationId, memberIds);
  }

  public void removeMember(UUID conversationId, UUID userId) {
    conversationMemberRepository.removeMember(conversationId, userId);

    evictMemberIdsCache(conversationId);
    memberCacheService.removeMember(conversationId, userId);
  }

  public void updateRole(UUID conversationId, UUID userId, ConversationRole role) {
    conversationMemberRepository.updateRole(conversationId, userId, role);
  }

  public void leaveConversation(UUID conversationId, UUID userId) {

    ConversationMember member =
        conversationMemberRepository.findByConversationIdAndUserId(conversationId, userId)
            .orElseThrow(() -> new NotFoundException(ConversationMember.class, userId));
    List<ConversationMember> members =
        conversationMemberRepository.findAllByConversationId(conversationId);

    if (members.size() == 1) {
      removeMember(conversationId, userId);
      conversationRepository.deleteById(conversationId);
      memberCacheService.evict(conversationId);
      return;
    }

    if (member.getRole() == ConversationRole.ADMIN) {
      Long countAdmin = conversationMemberRepository.countByConversationIdAndRole(conversationId,
          ConversationRole.ADMIN);
      if (countAdmin == 1) {
        List<ConversationMember> otherAdmins =
            members.stream().filter(m -> !m.getUserId().equals(userId)).toList();
        ConversationMember newAdmin = otherAdmins.get(0);
        updateRole(conversationId, newAdmin.getUserId(), ConversationRole.ADMIN);
      }
    }
    removeMember(conversationId, userId);
    // TODO: notification to member

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
  public void evictMemberIdsCache(UUID conversationId) {
  }

}
