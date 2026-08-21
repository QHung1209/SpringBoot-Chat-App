package com.mcxx.chat.chat.security;

import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import com.mcxx.chat.chat.annotation.IsAdmin;
import com.mcxx.chat.chat.annotation.IsMember;
import com.mcxx.chat.chat.security.guard.IsAdminGuard;
import com.mcxx.chat.chat.security.guard.IsMemberGuard;
import com.mcxx.chat.common.util.ExtractParam;
import com.mcxx.chat.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class ChatSecurityAspect {
  private final IsMemberGuard isMemberGuard;
  private final IsAdminGuard isAdminGuard;

  @Before("@annotation(isAdmin)")
  public void handleIsAdmin(JoinPoint joinPoint, IsAdmin isAdmin) {
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    UUID conversationId = ExtractParam.extractUUIDParam(joinPoint, "conversationId");
    if (conversationId != null && currentUserId != null) {

      isAdminGuard.check(conversationId, currentUserId);
    }


  }

  @Before("@annotation(isMember)")
  public void handleIsMember(JoinPoint joinPoint, IsMember isMember) {
    UUID currentUserId = SecurityUtils.getCurrentUserId();

    UUID conversationId = ExtractParam.extractUUIDParam(joinPoint, "conversationId");
    if (conversationId != null && currentUserId != null) {

      isMemberGuard.check(conversationId, currentUserId);
    }
  }
}
