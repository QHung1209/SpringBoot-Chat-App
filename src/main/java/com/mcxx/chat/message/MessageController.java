package com.mcxx.chat.message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
      @AuthenticationPrincipal AuthUser authUser,
      @Valid @RequestBody SendMessageRequest request) {
    Message message = messageService.sendMessage(authUser.getId(), request);
    return ResponseEntity.ok(ApiResponse.success(200, MessageResponse.from(message)));
  }

  @GetMapping("/{conversationId}")
  public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
      @AuthenticationPrincipal AuthUser authUser,
      @PathVariable UUID conversationId,
      @RequestParam(required = false) Instant cursor) {
    List<Message> messages = messageService.getMessages(conversationId, cursor);
    List<MessageResponse> response = messages.stream()
        .map(MessageResponse::from)
        .toList();
    return ResponseEntity.ok(ApiResponse.success(200, response));
  }
}
