package com.mcxx.chat.common.cache;

import java.util.UUID;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import com.mcxx.chat.user.domain.User;
import com.mcxx.chat.user.repository.UserRepository;
import com.mcxx.chat.user.dto.response.UserBasicInfo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCacheService {
  private final UserRepository userRepository;

  @Cacheable(value = "userBasic", key = "#id")
  public UserBasicInfo getUserBasicInfo(@NonNull UUID id) {
    User u = userRepository.findById(id).orElseThrow();
    return new UserBasicInfo(u.getId(), u.getUsername(), u.getFirstName(), u.getLastName(),
        u.getEmail(), u.getAvatarUrl());
  }
}
