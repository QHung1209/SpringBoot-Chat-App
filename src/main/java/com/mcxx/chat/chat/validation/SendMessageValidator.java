package com.mcxx.chat.chat.validation;

import com.mcxx.chat.chat.domain.MessageType;
import com.mcxx.chat.chat.dto.request.SendMessageRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SendMessageValidator
    implements ConstraintValidator<ValidSendMessage, SendMessageRequest> {

  @Override
  public boolean isValid(SendMessageRequest request, ConstraintValidatorContext context) {

    if (request == null) {
      return true;
    }

    if (request.getReceiverId() == null && request.getConversationId() == null) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("receiverId or conversationId is required")
          .addPropertyNode("receiverId").addConstraintViolation();
      return false;
    }

    switch (request.getType()) {
      case MessageType.TEXT -> {
        if (request.getContent() == null || request.getContent().isBlank()) {
          context.disableDefaultConstraintViolation();
          context.buildConstraintViolationWithTemplate("content is required for TEXT message")
              .addPropertyNode("content").addConstraintViolation();
          return false;
        }
      }
      case MessageType.IMAGE, MessageType.VIDEO, MessageType.FILE -> {
        if (request.getMediaIds() == null || request.getMediaIds().isEmpty()) {
          context.disableDefaultConstraintViolation();
          context.buildConstraintViolationWithTemplate("mediaIds is required for " + request.getType() + " message")
              .addPropertyNode("mediaIds").addConstraintViolation();
          return false;
        }
      }
      case SYSTEM -> {
      }
    }

    return true;
  }
}
