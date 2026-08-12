package com.mcxx.chat.conversation.application;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationMemberCacheService {

  private static final String KEY_PREFIX = "conv-members:";
  private static final Duration TTL = Duration.ofMinutes(30);

  private final RedisTemplate<String, String> redisTemplate;

  private String key(UUID conversationId) {
    return KEY_PREFIX + conversationId;
  }

  private boolean isPopulated(String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

  public void populate(UUID conversationId, List<UUID> memberIds) {
    String key = key(conversationId);
    String[] values = memberIds.stream().map(UUID::toString).toArray(String[]::new);
    redisTemplate.opsForSet().add(key, values);
    redisTemplate.expire(key, TTL);
  }

  public Boolean isMember(UUID conversationId, UUID userId) {
    String key = key(conversationId);
    if (!isPopulated(key)) {
      return null;
    }
    return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId.toString()));
  }

  public void addMembers(UUID conversationId, List<UUID> memberIds) {
    String key = key(conversationId);
    if (!isPopulated(key))
      return;
    String[] values = memberIds.stream().map(UUID::toString).toArray(String[]::new);
    redisTemplate.opsForSet().add(key, values);
  }

  public void removeMember(UUID conversationId, UUID userId) {
    String key = key(conversationId);
    redisTemplate.opsForSet().remove(key, userId.toString());
  }

  public void evict(UUID conversationId) {
    redisTemplate.delete(key(conversationId));
  }
}
