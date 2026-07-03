package com.mcxx.chat.userrelation;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.mcxx.chat.auth.dto.AuthUser;
import com.mcxx.chat.common.dto.ApiResponse;
import com.mcxx.chat.userrelation.dto.FriendView;
import com.mcxx.chat.userrelation.dto.RelationQuery;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/api/user-relations")
public class UserRelationController {
  private final UserRelationService userRelationService;

  @GetMapping()
  public ResponseEntity<ApiResponse<List<FriendView>>> getRelations(
      @AuthenticationPrincipal AuthUser authUser, @Valid RelationQuery query) {
    return ResponseEntity
        .ok(ApiResponse.success(200, userRelationService.getRelations(authUser.getId(), query)));
  }

  @PostMapping("")
  public ResponseEntity<ApiResponse<Void>> addRelation(@AuthenticationPrincipal AuthUser authUser,
      @RequestBody UUID targetId) {
    userRelationService.addRelation(authUser.getId(), targetId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("my-requests")
  public ResponseEntity<ApiResponse<List<FriendView>>> getMyRequests(
      @AuthenticationPrincipal AuthUser authUser, @RequestParam(required = false) UUID relationId) {
    return ResponseEntity.ok(
        ApiResponse.success(200, userRelationService.getMyRequests(authUser.getId(), relationId)));
  }

  @PostMapping("/block")
  public ResponseEntity<ApiResponse<Void>> blockUser(@AuthenticationPrincipal AuthUser authUser,
      @RequestBody UUID targetId) {
    userRelationService.blockUser(authUser.getId(), targetId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/unblock")
  public ResponseEntity<ApiResponse<Void>> unblockUser(@AuthenticationPrincipal AuthUser authUser,
      @RequestBody UUID targetId) {
    userRelationService.deleteRelation(authUser.getId(), targetId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/unfriend")
  public ResponseEntity<ApiResponse<Void>> unFriend(@AuthenticationPrincipal AuthUser authUser,
      @RequestBody UUID targetId) {
    userRelationService.deleteRelation(authUser.getId(), targetId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/accept")
  public ResponseEntity<ApiResponse<Void>> acceptUser(@AuthenticationPrincipal AuthUser authUser,
      @RequestBody UUID targetId) {
    userRelationService.acceptUser(authUser.getId(), targetId);
    return ResponseEntity.ok().build();
  }



}
