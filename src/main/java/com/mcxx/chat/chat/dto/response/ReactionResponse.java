package com.mcxx.chat.chat.dto.response;

import java.util.UUID;
import com.mcxx.chat.chat.repository.projection.ReactionSummaryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReactionResponse {
  private UUID messageId;
  private String reaction;
  private Long count;
  private Boolean reacted;

  public static ReactionResponse from(ReactionSummaryProjection projection) {
    ReactionResponse resp = new ReactionResponse();
    resp.setMessageId(projection.getMessageId());
    resp.setReaction(projection.getReaction());
    resp.setCount(projection.getCount());
    resp.setReacted(projection.getReacted());
    return resp;
  }
}
