package com.mcxx.chat.chat.api;

import com.mcxx.chat.chat.domain.MessageType;
import com.mcxx.chat.chat.annotation.IsMember;
import com.mcxx.chat.chat.application.ConversationService;
import com.mcxx.chat.chat.application.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mcxx.chat.auth.dto.response.AuthUser;
import com.mcxx.chat.common.dto.ApiResponse;
import com.mcxx.chat.chat.dto.request.CreateDirectConversationRequest;
import com.mcxx.chat.chat.dto.request.CreateGroupRequest;
import com.mcxx.chat.chat.dto.request.SendMessageRequest;
import com.mcxx.chat.chat.dto.request.UpdateGroupRequest;
import com.mcxx.chat.chat.dto.response.ConversationResponse;
import com.mcxx.chat.chat.dto.response.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/conversations")
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

  @IsMember
  @GetMapping("/{conversationId}")
  public ResponseEntity<ApiResponse<ConversationResponse>> detail(@PathVariable UUID conversationId) {
    return ResponseEntity
        .ok(ApiResponse.success(200, ConversationResponse.from(conversationService.detail(conversationId))));
  }

  @IsMember
  @GetMapping("/{conversationId}/messages")
  public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
      @AuthenticationPrincipal AuthUser authUser, @PathVariable UUID conversationId,
      @RequestParam(required = false) Instant before, @RequestParam(required = false) Instant after) {
    return ResponseEntity
        .ok(ApiResponse.success(200, messageService.getMessages(authUser.getId(), conversationId,
            before, after)));
  }

  @IsMember
  @PostMapping("/{conversationId}/messages")
  public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
      @AuthenticationPrincipal AuthUser authUser,
      @PathVariable UUID conversationId,
      @Valid @RequestBody SendMessageRequest request) {
    MessageResponse response = messageService.sendMessage(authUser.getId(), request, conversationId);
    return ResponseEntity.ok(ApiResponse.success(200, response));
  }

  @IsMember
  @PostMapping("/{conversationId}/messages/{messageId}/seen")
  public ResponseEntity<ApiResponse<Void>> seenMessage(
      @AuthenticationPrincipal AuthUser authUser,
      @PathVariable UUID conversationId,
      @PathVariable UUID messageId) {
    messageService.seenMessage(authUser.getId(), messageId);
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @IsMember
  @PostMapping("/{conversationId}/messages/{messageId}/pin")
  public ResponseEntity<ApiResponse<Void>> pinMessage(
      @AuthenticationPrincipal AuthUser authUser,
      @PathVariable UUID conversationId,
      @PathVariable UUID messageId) {
    messageService.pinMessage(authUser.getId(), messageId);
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @IsMember
  @PostMapping("/{conversationId}/messages/{messageId}/unpin")
  public ResponseEntity<ApiResponse<Void>> unpinMessage(
      @AuthenticationPrincipal AuthUser authUser,
      @PathVariable UUID conversationId,
      @PathVariable UUID messageId) {
    messageService.pinMessage(authUser.getId(), messageId);
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @IsMember
  @DeleteMapping("/{conversationId}/messages/{messageId}")
  public ResponseEntity<ApiResponse<Void>> deleteMessage(
      @AuthenticationPrincipal AuthUser authUser,
      @PathVariable UUID conversationId,
      @PathVariable UUID messageId) {
    messageService.deleteMessage(authUser.getId(), messageId);
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @PostMapping("/create-direct")
  public ResponseEntity<ApiResponse<ConversationResponse>> createDirectConversation(
      @AuthenticationPrincipal AuthUser authUser,
      @Valid @RequestBody CreateDirectConversationRequest request) {
    return ResponseEntity.ok(ApiResponse.success(200,
        ConversationResponse.from(
            conversationService.createDirectConversation(authUser.getId(), request.getOtherUserId()))));
  }

  @PostMapping("/create-group")
  public ResponseEntity<ApiResponse<ConversationResponse>> createGroupConversation(
      @AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody CreateGroupRequest request) {
    return ResponseEntity.ok(ApiResponse.success(200,
        ConversationResponse.from(conversationService.createGroupConversation(authUser.getId(), request))));
  }

  @PutMapping("/{conversationId}")
  public ResponseEntity<ApiResponse<Void>> updateGroupInfo(@PathVariable UUID conversationId,
      @Valid @RequestBody UpdateGroupRequest request) {
    conversationService.updateGroupInfo(conversationId, request);
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @IsMember
  @PostMapping("/{conversationId}/seen")
  public ResponseEntity<ApiResponse<Void>> seenConversation(
      @AuthenticationPrincipal AuthUser authUser,
      @PathVariable UUID conversationId,
      @RequestParam(required = false) UUID messageId) {
    messageService.seenConversation(authUser.getId(), conversationId, messageId);
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @IsMember
  @GetMapping("/{conversationId}/media")
  public ResponseEntity<ApiResponse<List<MessageResponse>>> getMediaMessages(
      @AuthenticationPrincipal AuthUser authUser,
      @PathVariable UUID conversationId,
      @RequestParam(required = false) MessageType type,
      @RequestParam(required = false) Instant before) {
    return ResponseEntity.ok(ApiResponse.success(200,
        messageService.getMediaMessages(authUser.getId(), conversationId, type, before)));
  }

  @IsMember
  @GetMapping("/{conversationId}/pinned")
  public ResponseEntity<ApiResponse<List<MessageResponse>>> getPinnedMessages(
      @AuthenticationPrincipal AuthUser authUser, @PathVariable UUID conversationId,
      @RequestParam(required = false) Instant before) {
    return ResponseEntity.ok(ApiResponse.success(200,
        messageService.getPinnedMessages(conversationId, before)));
  }
}
