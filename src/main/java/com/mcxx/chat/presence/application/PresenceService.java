package com.mcxx.chat.presence.application;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PresenceService {
  private static final Duration SESSION_TTL = Duration.ofSeconds(90);
  private final StringRedisTemplate redisTemplate;

  private static final String USER_SESSION_KEY = "user:device:";
  private static final String USER_LAST_SEEN = "user:last_seen:";


  public boolean addSession(UUID userId, String sessionId) {
    String key = USER_SESSION_KEY + userId;
    redisTemplate.opsForSet().add(key, sessionId);
    redisTemplate.expire(key, SESSION_TTL);

    Long size = redisTemplate.opsForSet().size(key);
    return size != null && size == 1;
  }

  public boolean removeSession(UUID userId, String sessionId) {
    String key = USER_SESSION_KEY + userId;
    redisTemplate.opsForSet().remove(key, sessionId);

    Long size = redisTemplate.opsForSet().size(key);
    boolean isNowOffline = (size == null || size == 0);
    if (isNowOffline) {
      redisTemplate.delete(key);
      String lastSeenKey = USER_LAST_SEEN + userId;
      redisTemplate.opsForValue().set(lastSeenKey, String.valueOf(Instant.now().toEpochMilli()));
    }
    return isNowOffline;
  }

  public boolean isUserOnline(UUID userId) {
    String key = USER_SESSION_KEY + userId;
    Long count = redisTemplate.opsForSet().size(key);

    return count != null && count > 0;
  }

  public Long getLastSeen(UUID userId) {
    String key = USER_LAST_SEEN + userId;
    String val = redisTemplate.opsForValue().get(key);
    return val != null ? Long.parseLong(val) : null;
  }


}
