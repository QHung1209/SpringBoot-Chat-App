package com.mcxx.chat.common.exception;

public class ForbiddenException extends RuntimeException {

  public ForbiddenException(Class<?> type, Object id) {
    super("%s with id '%s' not allowed to perform this action".formatted(type.getSimpleName(), id));
  }
}
