package com.mcxx.chat.message;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mcxx.chat.auth.dto.AuthUser;
import com.mcxx.chat.common.dto.ApiResponse;
import com.mcxx.chat.message.dto.request.SendMessageRequest;
import com.mcxx.chat.message.dto.response.MessageResponse;
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
    Message message = messageService.sendMessage(authUser.getId(), request);
    return ResponseEntity.ok(ApiResponse.success(200, MessageResponse.from(message)));
  }

  @DeleteMapping("/{messageId}")
  public ResponseEntity<ApiResponse<Void>> deleteMessage(@AuthenticationPrincipal AuthUser authUser,
      @PathVariable UUID messageId) {
    messageService.deleteMessage(authUser.getId(), messageId);
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }
}
