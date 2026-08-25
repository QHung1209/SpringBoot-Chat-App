package com.mcxx.chat.chat.application;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.mcxx.chat.chat.domain.ConversationRole;
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

  public void populate(UUID conversationId, Map<UUID, ConversationRole> memberRoles) {
    if (memberRoles == null || memberRoles.isEmpty()) {
      return;
    }
    String key = key(conversationId);
    Map<String, String> entries = memberRoles.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().name()));
    redisTemplate.opsForHash().putAll(key, entries);
    redisTemplate.expire(key, TTL);
  }

  public Boolean isMember(UUID conversationId, UUID userId) {
    String key = key(conversationId);
    if (!isPopulated(key)) {
      return null;
    }
    return redisTemplate.opsForHash().hasKey(key, userId.toString());
  }

  public List<UUID> getMemberIds(UUID conversationId) {
    String key = key(conversationId);
    if (!isPopulated(key)) {
      return null;
    }
    java.util.Set<Object> keys = redisTemplate.opsForHash().keys(key);
    return keys.stream().map(k -> UUID.fromString(k.toString())).toList();
  }

  public ConversationRole getRole(UUID conversationId, UUID userId) {
    String key = key(conversationId);
    if (!isPopulated(key)) {
      return null;
    }
    Object roleObj = redisTemplate.opsForHash().get(key, userId.toString());
    if (roleObj == null) {
      return null;
    }
    return ConversationRole.valueOf(roleObj.toString());
  }

  public void addMembers(UUID conversationId, Map<UUID, ConversationRole> memberRoles) {
    String key = key(conversationId);
    if (!isPopulated(key) || memberRoles == null || memberRoles.isEmpty()) {
      return;
    }
    Map<String, String> entries = memberRoles.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().name()));
    redisTemplate.opsForHash().putAll(key, entries);
  }

  public void updateRole(UUID conversationId, UUID userId, ConversationRole role) {
    String key = key(conversationId);
    if (!isPopulated(key)) {
      return;
    }
    redisTemplate.opsForHash().put(key, userId.toString(), role.name());
  }

  public void removeMember(UUID conversationId, UUID userId) {
    String key = key(conversationId);
    redisTemplate.opsForHash().delete(key, userId.toString());
  }

  public void evict(UUID conversationId) {
    redisTemplate.delete(key(conversationId));
  }
}
