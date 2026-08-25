package com.mcxx.chat.presence.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mcxx.chat.common.dto.ApiResponse;
import com.mcxx.chat.presence.application.PresenceService;
import com.mcxx.chat.presence.dto.response.PresenceResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users/presence")
@RequiredArgsConstructor
public class PresenceController {
  private final PresenceService presenceService;

  @GetMapping
  public ResponseEntity<ApiResponse<Map<UUID, PresenceResponse>>> getUsersPresence(
      @RequestParam List<UUID> userIds) {
    Map<UUID, PresenceResponse> result = new HashMap<>();
    for (UUID userId : userIds) {
      boolean online = presenceService.isUserOnline(userId);
      Long lastSeen = online ? null : presenceService.getLastSeen(userId);
      result.put(userId, new PresenceResponse(online, lastSeen));
    }
    return ResponseEntity.ok(ApiResponse.success(200, result));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<PresenceResponse>> getUserPresence(@PathVariable UUID userId) {
    boolean online = presenceService.isUserOnline(userId);
    Long lastSeen = online ? null : presenceService.getLastSeen(userId);
    return ResponseEntity.ok(ApiResponse.success(200, new PresenceResponse(online, lastSeen)));
  }
}
