package com.mcxx.chat.websocket;

import java.util.UUID;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import com.mcxx.chat.auth.dto.response.AuthUser;
import com.mcxx.chat.auth.jwt.JwtAuthConverter;
import com.mcxx.chat.conversation.application.ConversationMemberService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtStompChannelInterceptor implements ChannelInterceptor {
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtDecoder jwtDecoder;
  private final JwtAuthConverter jwtAuthConverter;
  private final ConversationMemberService conversationMemberService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (accessor == null) {
      return message;
    }

    if (accessor.getCommand() == StompCommand.CONNECT) {
      authenticateConnect(accessor);
    }

    if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
      authorizeSubscribe(accessor);
    }

    return message;
  }

  private void authenticateConnect(StompHeaderAccessor accessor) {
    String authorization = accessor.getFirstNativeHeader("Authorization");
    if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
      throw new AccessDeniedException("Missing bearer token");
    }

    String token = authorization.substring(BEARER_PREFIX.length());
    Jwt jwt = jwtDecoder.decode(token);
    AbstractAuthenticationToken authentication = jwtAuthConverter.convert(jwt);
    accessor.setUser(authentication);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private void authorizeSubscribe(StompHeaderAccessor accessor) {
    String destination = accessor.getDestination();

    if (destination == null) {
      throw new AccessDeniedException("Missing destination");
    }

    if (!destination.startsWith("/topic/conversations/")) {
      return;
    }

    String conversationIdText = destination.substring("/topic/conversations/".length());
    int suffixIndex = conversationIdText.indexOf('/');
    if (suffixIndex >= 0) {
      conversationIdText = conversationIdText.substring(0, suffixIndex);
    }

    UUID conversationId = UUID.fromString(conversationIdText);

    Authentication authentication = (Authentication) accessor.getUser();
    if (authentication == null || !(authentication.getPrincipal() instanceof AuthUser authUser)) {
      throw new AccessDeniedException("Unauthenticated websocket subscription");
    }

    boolean isMember = conversationMemberService.isMember(conversationId, authUser.getId());
    if (!isMember) {
      throw new AccessDeniedException("User is not a member of this conversation");
    }
  }
}
