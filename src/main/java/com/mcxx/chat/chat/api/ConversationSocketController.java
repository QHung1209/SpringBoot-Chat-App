package com.mcxx.chat.chat.api;

import com.mcxx.chat.chat.application.ChatRealtimeService;
import java.security.Principal;
import java.util.UUID;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import com.mcxx.chat.auth.dto.response.AuthUser;
import com.mcxx.chat.chat.dto.request.TypingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ConversationSocketController {
  private final ChatRealtimeService chatRealtimeService;

  @MessageMapping("/conversations/{conversationId}/typing")
  public void typing(
      Principal principal,
      @DestinationVariable UUID conversationId,
      @Valid TypingRequest request) {
    if (!(principal instanceof Authentication authentication)
        || !(authentication.getPrincipal() instanceof AuthUser authUser)) {
      throw new AccessDeniedException("Unauthenticated websocket message");
    }

    chatRealtimeService.publishTyping(authUser, conversationId, request.typing());
  }
}
