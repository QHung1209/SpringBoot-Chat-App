package com.mcxx.chat.user;

import java.util.UUID;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import com.mcxx.chat.user.dto.UserBasicInfo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCacheService {
  private final UserRepository userRepository;

  @Cacheable(value = "userBasic", key = "#id")
  public UserBasicInfo getUserBasicInfo(@NonNull UUID id) {
    User u = userRepository.findById(id).orElseThrow();
    return new UserBasicInfo(u.getId(), u.getUsername(), u.getFullName(), u.getEmail(),
        u.getAvatarUrl());
  }
}
