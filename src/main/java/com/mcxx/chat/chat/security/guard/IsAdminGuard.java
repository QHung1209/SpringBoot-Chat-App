package com.mcxx.chat.chat.security.guard;

import java.util.UUID;
import org.springframework.stereotype.Component;

import com.mcxx.chat.chat.application.ConversationMemberService;
import com.mcxx.chat.chat.domain.Conversation;
import com.mcxx.chat.common.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IsAdminGuard {

  private final ConversationMemberService conversationMemberService;

  public void check(UUID conversationId, UUID userId) {
    String role = conversationMemberService.memberRole(conversationId, userId).name();
    if (!role.equals("ADMIN")) {
      throw new ForbiddenException(Conversation.class, conversationId);
    }
  }
}
