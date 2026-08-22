package com.mcxx.chat.userrelation.api;

import com.mcxx.chat.userrelation.application.UserRelationService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mcxx.chat.auth.dto.response.AuthUser;
import com.mcxx.chat.common.dto.ApiResponse;
import com.mcxx.chat.userrelation.dto.response.FriendView;
import com.mcxx.chat.userrelation.dto.request.RelationQuery;
import com.mcxx.chat.userrelation.dto.request.TargetUserRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user-relations")
public class UserRelationController {
  private final UserRelationService userRelationService;

  @GetMapping
  public ResponseEntity<ApiResponse<List<FriendView>>> getRelations(
      @AuthenticationPrincipal AuthUser authUser, @Valid RelationQuery query) {
    return ResponseEntity
        .ok(ApiResponse.success(200, userRelationService.getRelations(authUser.getId(), query)));
  }

  @PostMapping
  public ResponseEntity<ApiResponse<Void>> addRelation(@AuthenticationPrincipal AuthUser authUser,
      @Valid @RequestBody TargetUserRequest request) {
    userRelationService.addRelation(authUser.getId(), request.getTargetId());
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @GetMapping("/my-requests")
  public ResponseEntity<ApiResponse<List<FriendView>>> getMyRequests(
      @AuthenticationPrincipal AuthUser authUser, @RequestParam(required = false) UUID relationId) {
    return ResponseEntity.ok(
        ApiResponse.success(200, userRelationService.getMyRequests(authUser.getId(), relationId)));
  }

  @PostMapping("/block")
  public ResponseEntity<ApiResponse<Void>> blockUser(@AuthenticationPrincipal AuthUser authUser,
      @Valid @RequestBody TargetUserRequest request) {
    userRelationService.blockUser(authUser.getId(), request.getTargetId());
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @PostMapping("/unblock")
  public ResponseEntity<ApiResponse<Void>> unblockUser(@AuthenticationPrincipal AuthUser authUser,
      @Valid @RequestBody TargetUserRequest request) {
    userRelationService.deleteRelation(authUser.getId(), request.getTargetId());
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @PostMapping("/unfriend")
  public ResponseEntity<ApiResponse<Void>> unFriend(@AuthenticationPrincipal AuthUser authUser,
      @Valid @RequestBody TargetUserRequest request) {
    userRelationService.deleteRelation(authUser.getId(), request.getTargetId());
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }

  @PostMapping("/accept")
  public ResponseEntity<ApiResponse<Void>> acceptUser(@AuthenticationPrincipal AuthUser authUser,
      @Valid @RequestBody TargetUserRequest request) {
    userRelationService.acceptUser(authUser.getId(), request.getTargetId());
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }



  @GetMapping("/incoming-requests")
  public ResponseEntity<ApiResponse<List<FriendView>>> getIncomingRequests(
      @AuthenticationPrincipal AuthUser authUser, @RequestParam(required = false) UUID relationId) {
    return ResponseEntity.ok(
        ApiResponse.success(200, userRelationService.getIncomingRequests(authUser.getId(), relationId)));
  }

  @GetMapping("/status")
  public ResponseEntity<ApiResponse<List<com.mcxx.chat.userrelation.dto.response.UserRelationStatusView>>> getRelationsStatus(
      @AuthenticationPrincipal AuthUser authUser, @RequestParam List<UUID> targetUserIds) {
    return ResponseEntity.ok(
        ApiResponse.success(200, userRelationService.getRelationsWithUsers(authUser.getId(), targetUserIds)));
  }

  @GetMapping("/status/{targetId}")
  public ResponseEntity<ApiResponse<com.mcxx.chat.userrelation.dto.response.UserRelationStatusView>> getRelationStatus(
      @AuthenticationPrincipal AuthUser authUser, @org.springframework.web.bind.annotation.PathVariable UUID targetId) {
    return ResponseEntity.ok(
        ApiResponse.success(200, userRelationService.getRelationWithUser(authUser.getId(), targetId)));
  }
}

