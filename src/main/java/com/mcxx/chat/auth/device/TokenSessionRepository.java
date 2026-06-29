package com.mcxx.chat.auth.device;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface TokenSessionRepository extends CrudRepository<TokenSession, UUID> {
  void deleteByUserId(UUID userId);
}
