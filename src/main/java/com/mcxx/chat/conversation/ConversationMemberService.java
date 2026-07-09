package com.mcxx.chat.conversation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.mcxx.chat.common.exception.NotFoundException;
import com.mcxx.chat.conversation.constants.ConversationRole;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationMemberService {

  private final ConversationMemberRepository conversationMemberRepository;
  private final ConversationRepository conversationRepository;

  public List<ConversationMember> getMembers(UUID conversationId, Instant createdAt) {
    return conversationMemberRepository.findAllByConversationIdOrderByCreatedAtAsc(conversationId,
        createdAt);
  }

  public void addMembers(UUID conversationId, List<UUID> memberIds) {
    List<ConversationMember> members =
        memberIds.stream().map(id -> new ConversationMember(conversationId, id)).toList();
    conversationMemberRepository.saveAll(members);
  }

  public void removeMember(UUID conversationId, UUID userId) {
    conversationMemberRepository.removeMember(conversationId, userId);
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

}
