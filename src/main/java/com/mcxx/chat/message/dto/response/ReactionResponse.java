package com.mcxx.chat.message.dto.response;

import java.util.UUID;
import com.mcxx.chat.message.projection.ReactionSummaryProjection;
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
