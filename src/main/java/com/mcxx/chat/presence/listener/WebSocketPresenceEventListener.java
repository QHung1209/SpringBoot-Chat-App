package com.mcxx.chat.presence.listener;

import com.mcxx.chat.auth.dto.response.AuthUser;
import com.mcxx.chat.presence.application.PresenceService;
import com.mcxx.chat.presence.event.UserPresenceWsEvent;
import com.mcxx.chat.userrelation.application.UserRelationService;
import java.util.List;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketPresenceEventListener {

  private final PresenceService presenceService;
  private final SimpMessagingTemplate messagingTemplate;
  private final UserRelationService userRelationService;

  @EventListener
  public void handleSessionConnected(SessionConnectedEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    if (accessor.getUser() instanceof Authentication auth
        && auth.getPrincipal() instanceof AuthUser user) {

      boolean isFirstSession = presenceService.addSession(user.getId(), accessor.getSessionId());
      if (isFirstSession) {
        notifyFriends(user.getId(), true, null);
      }
    }
  }

  @EventListener
  public void handleSessionDisconnect(SessionDisconnectEvent event) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
    if (accessor.getUser() instanceof Authentication auth
        && auth.getPrincipal() instanceof AuthUser user) {

      boolean isFullyOffline = presenceService.removeSession(user.getId(), accessor.getSessionId());
      if (isFullyOffline) {
        Long lastSeen = presenceService.getLastSeen(user.getId());
        notifyFriends(user.getId(), false, lastSeen);
      }
    }
  }

  private void notifyFriends(UUID userId, boolean online, Long lastSeen) {
    List<UUID> friendIds = userRelationService.getFriendIds(userId);
    UserPresenceWsEvent payload = new UserPresenceWsEvent(userId, online, lastSeen);
    for (UUID friendId : friendIds) {
      messagingTemplate.convertAndSendToUser(friendId.toString(), "/queue/presence", payload);
    }
  }
}
