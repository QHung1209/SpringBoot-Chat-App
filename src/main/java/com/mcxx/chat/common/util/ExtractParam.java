package com.mcxx.chat.common.util;

import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class ExtractParam {

  public static UUID extractUUIDParam(JoinPoint joinPoint, String paramName) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String[] parameterNames = signature.getParameterNames();
    Object[] args = joinPoint.getArgs();
    if (parameterNames != null) {
      for (int i = 0; i < parameterNames.length; i++) {
        if (parameterNames[i].equals(paramName) && args[i] instanceof UUID uuid) {
          return uuid;
        }
      }
    }
    return null;
  }
}
