package com.mcxx.chat.media.api;

import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mcxx.chat.auth.dto.response.AuthUser;
import com.mcxx.chat.common.dto.ApiResponse;
import com.mcxx.chat.media.application.MediaService;
import com.mcxx.chat.media.dto.request.GeneratePresignedUrlRequest;
import com.mcxx.chat.media.dto.response.PresignedUrlResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {
  private final MediaService mediaService;

  @PostMapping("/presigned-url")
  public ResponseEntity<ApiResponse<PresignedUrlResponse>> generatePresignedUrl(
      @AuthenticationPrincipal AuthUser authUser,
      @Valid @RequestBody GeneratePresignedUrlRequest request) {
    return ResponseEntity
        .ok(ApiResponse.success(200, mediaService.generatePresignedUrl(authUser.getId(), request)));
  }

  @DeleteMapping("/{mediaId}")
  public ResponseEntity<ApiResponse<Void>> deleteMedia(@AuthenticationPrincipal AuthUser authUser,
      @PathVariable UUID mediaId) {
    mediaService.deleteMedia(mediaId, authUser.getId());
    return ResponseEntity.ok(ApiResponse.success(200, null));
  }
}
