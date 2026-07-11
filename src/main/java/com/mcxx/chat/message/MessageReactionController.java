package com.mcxx.chat.message;

import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mcxx.chat.auth.dto.AuthUser;
import com.mcxx.chat.common.dto.ApiResponse;
import com.mcxx.chat.message.dto.request.ReactionRequest;
import com.mcxx.chat.message.dto.response.ReactionResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/messages/{messageId}/reactions")
@RequiredArgsConstructor
public class MessageReactionController {
  private final MessageReactionService messageReactionService;

  @PostMapping
  public ResponseEntity<ApiResponse<List<ReactionResponse>>> react(
      @AuthenticationPrincipal AuthUser authUser, @PathVariable UUID messageId,
      @Valid @RequestBody ReactionRequest request) {
    return ResponseEntity.ok(ApiResponse.success(200,
        messageReactionService.react(messageId, authUser.getId(), request.getReaction())));
  }

}
