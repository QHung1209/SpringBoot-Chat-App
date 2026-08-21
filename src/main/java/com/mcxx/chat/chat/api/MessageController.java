package com.mcxx.chat.chat.api;

import com.mcxx.chat.chat.application.MessageService;
import com.mcxx.chat.chat.dto.request.SendMessageRequest;
import com.mcxx.chat.chat.dto.response.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mcxx.chat.auth.dto.response.AuthUser;
import com.mcxx.chat.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {
  private final MessageService messageService;

  @PostMapping
  public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
      @AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody SendMessageRequest request) {
    MessageResponse response = messageService.sendMessage(authUser.getId(), request, null);
    return ResponseEntity.ok(ApiResponse.success(200, response));
  }
}
