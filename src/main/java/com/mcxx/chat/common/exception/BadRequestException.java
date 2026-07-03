package com.mcxx.chat.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadRequestException extends ResponseStatusException {

  public BadRequestException(String reason) {
    super(HttpStatus.CONFLICT, reason);
  }
}
