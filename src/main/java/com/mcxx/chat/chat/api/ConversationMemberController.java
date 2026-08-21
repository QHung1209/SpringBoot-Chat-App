package com.mcxx.chat.chat.api;

import com.mcxx.chat.auth.dto.response.AuthUser;
import com.mcxx.chat.chat.annotation.IsAdmin;
import com.mcxx.chat.chat.annotation.IsMember;
import com.mcxx.chat.chat.application.ConversationMemberService;
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
import com.mcxx.chat.common.dto.ApiResponse;
import com.mcxx.chat.chat.dto.request.AddMembersRequest;
import com.mcxx.chat.chat.dto.request.MemberIdRequest;
import com.mcxx.chat.chat.dto.request.UpdateRoleRequest;
import com.mcxx.chat.chat.dto.response.ConversationMemberResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/conversations/{conversationId}/members")
@RequiredArgsConstructor
public class ConversationMemberController {
  private final ConversationMemberService conversationMemberService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<ConversationMemberResponse>>> getMembers(
      @PathVariable UUID conversationId, @RequestParam(required = false) Instant createdAt) {
    return ResponseEntity.ok(ApiResponse.success(200,
        conversationMemberService.getMembers(conversationId, createdAt).stream()
            .map(ConversationMemberResponse::from).toList()));
  }

  @IsMember
  @PostMapping("/add")
  public ResponseEntity<ApiResponse<Void>> addMember(@AuthenticationPrincipal AuthUser user,
      @PathVariable UUID conversationId, @Valid @RequestBody AddMembersRequest request) {
    conversationMemberService.addMembers(user.getId(), conversationId, request.getMemberIds());
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @IsAdmin
  @PostMapping("/remove")
  public ResponseEntity<ApiResponse<Void>> removeMember(@AuthenticationPrincipal AuthUser user,
      @PathVariable UUID conversationId, @Valid @RequestBody MemberIdRequest request) {
    conversationMemberService.removeMember(user.getId(), conversationId, request.getMemberId());
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @IsMember
  @PostMapping("/leave")
  public ResponseEntity<ApiResponse<Void>> leave(@AuthenticationPrincipal AuthUser user,
      @PathVariable UUID conversationId) {
    conversationMemberService.leaveConversation(user.getId(), conversationId);
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @IsAdmin
  @PostMapping("/update-role")
  public ResponseEntity<ApiResponse<Void>> updateRole(@PathVariable UUID conversationId,
      @Valid @RequestBody UpdateRoleRequest request) {
    conversationMemberService.updateRole(conversationId, request.getMemberId(), request.getRole());
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }
}
