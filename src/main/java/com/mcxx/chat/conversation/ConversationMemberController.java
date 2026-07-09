package com.mcxx.chat.conversation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mcxx.chat.common.dto.ApiResponse;
import com.mcxx.chat.conversation.dto.request.AddMembersRequest;
import com.mcxx.chat.conversation.dto.request.UpdateRoleRequest;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/conversations/{conversationId}/members")
@RequiredArgsConstructor
public class ConversationMemberController {
  private final ConversationMemberService conversationMemberService;

  @GetMapping()
  public ResponseEntity<ApiResponse<List<ConversationMember>>> getMembers(
      @PathVariable UUID conversationId, @RequestParam(required = false) Instant createdAt) {
    return ResponseEntity.ok(
        ApiResponse.success(200, conversationMemberService.getMembers(conversationId, createdAt)));
  }

  @PostMapping("/add")
  public ResponseEntity<ApiResponse<Void>> addMember(@PathVariable UUID conversationId,
      @RequestBody AddMembersRequest request) {
    conversationMemberService.addMembers(conversationId, request.getMemberIds());
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @PostMapping("/remove")
  public ResponseEntity<ApiResponse<Void>> removeMember(@PathVariable UUID conversationId,
      @RequestBody UUID memberId) {
    conversationMemberService.removeMember(conversationId, memberId);
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @PostMapping("/leave")
  public ResponseEntity<ApiResponse<Void>> leave(@PathVariable UUID conversationId,
      @RequestBody UUID memberId) {
    conversationMemberService.leaveConversation(conversationId, memberId);
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @PostMapping("/update-role")
  public ResponseEntity<ApiResponse<Void>> updateRole(@PathVariable UUID conversationId,
      @RequestBody UpdateRoleRequest request) {
    conversationMemberService.updateRole(conversationId, request.getMemberId(), request.getRole());
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }
}
