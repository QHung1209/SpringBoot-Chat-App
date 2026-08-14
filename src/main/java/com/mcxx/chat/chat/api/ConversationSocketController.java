package com.mcxx.chat.chat.api;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import com.mcxx.chat.auth.dto.response.AuthUser;
import com.mcxx.chat.chat.application.ConversationMemberService;
import com.mcxx.chat.chat.dto.request.TypingRequest;
import com.mcxx.chat.chat.event.TypingEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ConversationSocketController {

  private final ConversationMemberService conversationMemberService;
  private final ApplicationEventPublisher eventPublisher;

  @MessageMapping("/conversations/{conversationId}/typing")
  public void typing(
      Principal principal,
      @DestinationVariable UUID conversationId,
      @Valid TypingRequest request) {
    if (!(principal instanceof Authentication authentication)
        || !(authentication.getPrincipal() instanceof AuthUser authUser)) {
      throw new AccessDeniedException("Unauthenticated websocket message");
    }

    if (!conversationMemberService.isMember(conversationId, authUser.getId())) {
      throw new AccessDeniedException("User is not a member of this conversation");
    }

    eventPublisher.publishEvent(
        new TypingEvent(conversationId, authUser.getId(), authUser.getFullName(), request.typing(),
            Instant.now()));
  }
}
