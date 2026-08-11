package com.mcxx.chat.userrelation;

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
import com.mcxx.chat.auth.dto.AuthUser;
import com.mcxx.chat.common.dto.ApiResponse;
import com.mcxx.chat.userrelation.dto.FriendView;
import com.mcxx.chat.userrelation.dto.RelationQuery;
import com.mcxx.chat.userrelation.dto.TargetUserRequest;
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
    return ResponseEntity.ok().build();
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
    return ResponseEntity.ok().build();
  }

  @PostMapping("/unblock")
  public ResponseEntity<ApiResponse<Void>> unblockUser(@AuthenticationPrincipal AuthUser authUser,
      @Valid @RequestBody TargetUserRequest request) {
    userRelationService.deleteRelation(authUser.getId(), request.getTargetId());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/unfriend")
  public ResponseEntity<ApiResponse<Void>> unFriend(@AuthenticationPrincipal AuthUser authUser,
      @Valid @RequestBody TargetUserRequest request) {
    userRelationService.deleteRelation(authUser.getId(), request.getTargetId());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/accept")
  public ResponseEntity<ApiResponse<Void>> acceptUser(@AuthenticationPrincipal AuthUser authUser,
      @Valid @RequestBody TargetUserRequest request) {
    userRelationService.acceptUser(authUser.getId(), request.getTargetId());
    return ResponseEntity.ok().build();
  }



}
