package com.mcxx.chat.device.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcxx.chat.device.domain.TokenSession;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenSessionService {

  private static final String KEY_PREFIX = "session:";
  private final StringRedisTemplate redisTemplate;
  private final ObjectMapper objectMapper;

  public void saveSession(UUID deviceId, UUID userId, Integer tokenVersion, long ttlSeconds) {
    try {
      String key = KEY_PREFIX + deviceId;
      TokenSession session = new TokenSession(userId, tokenVersion);
      String json = objectMapper.writeValueAsString(session);
      redisTemplate.opsForValue().set(key, json, Duration.ofSeconds(ttlSeconds));
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize token session for device {}", deviceId, e);
    }
  }

  public TokenSession getSession(UUID deviceId) {
    try {
      String key = KEY_PREFIX + deviceId;
      String json = redisTemplate.opsForValue().get(key);
      if (json == null) {
        return null;
      }
      return objectMapper.readValue(json, TokenSession.class);
    } catch (JsonProcessingException e) {
      log.error("Failed to deserialize token session for device {}", deviceId, e);
      return null;
    }
  }

  public void deleteSession(UUID deviceId) {
    redisTemplate.delete(KEY_PREFIX + deviceId);
  }

  public void deleteSessions(List<UUID> deviceIds) {
    if (deviceIds == null || deviceIds.isEmpty()) {
      return;
    }
    List<String> keys = deviceIds.stream().map(id -> KEY_PREFIX + id).toList();
    redisTemplate.delete(keys);
  }
}
