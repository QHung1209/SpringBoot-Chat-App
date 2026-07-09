package com.mcxx.chat.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.mcxx.chat.auth.dto.AuthUser;
import com.mcxx.chat.common.dto.ApiResponse;
import com.mcxx.chat.user.dto.ChangePasswordRequest;
import com.mcxx.chat.user.dto.UpdateProfileRequest;
import com.mcxx.chat.user.dto.UserBasicInfo;
import com.mcxx.chat.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
      @AuthenticationPrincipal AuthUser authUser) {
    return ResponseEntity.ok(ApiResponse.success(200, userService.getProfile(authUser.getId())));
  }

  @PostMapping("/me")
  public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
      @AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody UpdateProfileRequest req) {
    return ResponseEntity
        .ok(ApiResponse.success(200, userService.updateProfile(authUser.getId(), req)));
  }

  @GetMapping("/search")
  public ResponseEntity<ApiResponse<List<UserBasicInfo>>> searchUsers(
      @AuthenticationPrincipal AuthUser authUser, @RequestParam(required = false) String q,
      @RequestParam(required = false) UUID cursor) {
    return ResponseEntity.ok(ApiResponse.success(200, userService.searchUsers(q, cursor)));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(
      @AuthenticationPrincipal AuthUser authUser, @PathVariable UUID userId) {
    return ResponseEntity.ok(ApiResponse.success(200, userService.getProfile(userId)));
  }

  @PostMapping("change-password")
  public ResponseEntity<ApiResponse<Void>> changePassword(
      @AuthenticationPrincipal AuthUser authUser, @Valid @RequestBody ChangePasswordRequest req) {
    userService.changePassword(authUser.getId(), req);
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }
}
