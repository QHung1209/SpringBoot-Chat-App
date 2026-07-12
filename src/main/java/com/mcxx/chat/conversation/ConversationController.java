package com.mcxx.chat.conversation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mcxx.chat.auth.dto.AuthUser;
import com.mcxx.chat.common.dto.ApiResponse;
import com.mcxx.chat.conversation.dto.request.CreateGroupRequest;
import com.mcxx.chat.conversation.dto.request.UpdateGroupRequest;
import com.mcxx.chat.conversation.dto.response.ConversationResponse;
import com.mcxx.chat.message.MessageService;
import com.mcxx.chat.message.dto.response.MessageResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {
  private final ConversationService conversationService;
  private final MessageService messageService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<ConversationResponse>>> getConversations(
      @AuthenticationPrincipal AuthUser authUser,
      @RequestParam(required = false) Instant updatedTime) {
    return ResponseEntity.ok(ApiResponse.success(200,
        conversationService.getConversations(authUser.getId(), updatedTime)));
  }

  @GetMapping("/{conversationId}")
  public ResponseEntity<ApiResponse<Conversation>> detail(@PathVariable UUID conversationId) {
    return ResponseEntity.ok(ApiResponse.success(200, conversationService.detail(conversationId)));
  }

  @GetMapping("/{conversationId}/messages")
  public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
      @AuthenticationPrincipal AuthUser authUser, @PathVariable UUID conversationId,
      @RequestParam(required = false) Instant cursor) {
    return ResponseEntity
        .ok(ApiResponse.success(200, messageService.getMessages(authUser.getId(), conversationId,
            cursor)));
  }

  @PostMapping("/create-direct")
  public ResponseEntity<ApiResponse<Conversation>> createDirectConversation(
      @AuthenticationPrincipal AuthUser authUser, @RequestBody UUID otherUserId) {
    return ResponseEntity.ok(ApiResponse.success(200,
        conversationService.createDirectConversation(authUser.getId(), otherUserId)));
  }

  @PostMapping("/create-group")
  public ResponseEntity<ApiResponse<Conversation>> createGroupConversation(
      @AuthenticationPrincipal AuthUser authUser, @RequestBody CreateGroupRequest request) {
    return ResponseEntity.ok(ApiResponse.success(200,
        conversationService.createGroupConversation(authUser.getId(), request)));
  }

  @PutMapping("/{conversationId}")
  public ResponseEntity<ApiResponse<Void>> updateGroupInfo(@PathVariable UUID conversationId,
      @RequestBody UpdateGroupRequest request) {
    conversationService.updateGroupInfo(conversationId, request);
    return ResponseEntity.ok().build();
  }
}
