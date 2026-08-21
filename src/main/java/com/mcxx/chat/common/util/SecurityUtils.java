package com.mcxx.chat.common.util;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.mcxx.chat.auth.dto.response.AuthUser;

public final class SecurityUtils {

  private SecurityUtils() {}

  public static AuthUser getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof AuthUser authUser) {
      return authUser;
    }
    return null;
  }

  public static UUID getCurrentUserId() {
    AuthUser user = getCurrentUser();
    return user != null ? user.getId() : null;
  }
}
