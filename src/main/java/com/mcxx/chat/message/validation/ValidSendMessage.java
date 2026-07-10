package com.mcxx.chat.message.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SendMessageValidator.class)
public @interface ValidSendMessage {
  String message() default "Invalid send message request";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
