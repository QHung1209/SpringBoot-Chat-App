package com.mcxx.chat.chat.dto.response;

import java.time.Instant;
import java.util.UUID;
import com.mcxx.chat.chat.domain.ConversationRole;
import com.mcxx.chat.chat.repository.projection.MemberProfileProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConversationMemberResponse {
  private UUID id;
  private String firstName;
  private String lastName;
  private String avatarUrl;
  private ConversationRole role;
  private Instant createdAt;
  private UUID lastSeenMessageId;

  public static ConversationMemberResponse from(MemberProfileProjection member) {
    return new ConversationMemberResponse(member.getId(), member.getFirstName(),
        member.getLastName(), member.getAvatarUrl(), member.getRole(), member.getCreatedAt(), member.getLastSeenMessageId());
  }
}
