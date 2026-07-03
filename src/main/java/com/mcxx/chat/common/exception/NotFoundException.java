package com.mcxx.chat.common.exception;

public class NotFoundException extends RuntimeException {

  public NotFoundException(Class<?> type, Object id) {
    super("%s with id '%s' not found".formatted(type.getSimpleName(), id));
  }
}
