package com.mcxx.chat.message.validation;

import com.mcxx.chat.message.dto.request.SendMessageRequest;
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
      case "TEXT" -> {
        if (request.getContent() == null || request.getContent().isBlank()) {
          context.disableDefaultConstraintViolation();
          context.buildConstraintViolationWithTemplate("content is required for TEXT message")
              .addPropertyNode("content").addConstraintViolation();
          return false;
        }
      }
      case "IMAGE", "VIDEO", "FILE" -> {
        if (request.getMediaId() == null) {
          context.disableDefaultConstraintViolation();
          context.buildConstraintViolationWithTemplate("mediaId is required for IMAGE message")
              .addPropertyNode("mediaId").addConstraintViolation();
          return false;
        }
      }
    }

    return true;
  }
}
