package com.mcxx.chat.device;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.util.UUID;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@RedisHash("session")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenSession {
  @Id
  private UUID deviceId;
  @Indexed
  private UUID userId;

  @TimeToLive
  private long ttlSeconds;
}
