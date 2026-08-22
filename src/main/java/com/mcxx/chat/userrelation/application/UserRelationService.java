package com.mcxx.chat.userrelation.application;

import com.mcxx.chat.userrelation.repository.UserRelationRepository;
import com.mcxx.chat.userrelation.domain.UserRelation;
import com.mcxx.chat.userrelation.domain.RelationStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.mcxx.chat.userrelation.dto.response.FriendView;
import com.mcxx.chat.userrelation.dto.request.RelationQuery;
import lombok.RequiredArgsConstructor;

import com.mcxx.chat.userrelation.dto.response.UserRelationStatusView;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRelationService {
  private final UserRelationRepository userRelationRepository;

  private record SortedIds(UUID lowId, UUID highId) {
  }

  private SortedIds getSortedIds(UUID userId, UUID targetId) {
    return userId.compareTo(targetId) < 0 ? new SortedIds(userId, targetId)
        : new SortedIds(targetId, userId);
  }

  public void addRelation(UUID userId, UUID targetId) {
    if (userId.equals(targetId)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add yourself as a friend");
    }

    SortedIds ids = getSortedIds(userId, targetId);
    UserRelation relation = new UserRelation();
    relation.setUserLowId(ids.lowId());
    relation.setUserHighId(ids.highId());
    relation.setActionUserId(userId);
    relation.setStatus(RelationStatus.PENDING);
    userRelationRepository.save(relation);
  }

  @Transactional(readOnly = true)
  public List<FriendView> getRelations(UUID userId, RelationQuery query) {
    return userRelationRepository.findRelations(userId, query.getRelationId(), query.getSearch(),
        query.getStatus().name());
  }

  public void blockUser(UUID userId, UUID targetId) {
    SortedIds ids = getSortedIds(userId, targetId);
    userRelationRepository.blockUser(ids.lowId(), ids.highId());
  }

  public void deleteRelation(UUID userId, UUID targetId) {
    SortedIds ids = getSortedIds(userId, targetId);
    userRelationRepository.deleteByUserLowIdAndUserHighId(ids.lowId(), ids.highId());
  }

  public void acceptUser(UUID userId, UUID targetId) {
    SortedIds ids = getSortedIds(userId, targetId);
    userRelationRepository.acceptUser(ids.lowId(), ids.highId());
  }

  @Transactional(readOnly = true)
  public List<FriendView> getMyRequests(UUID userId, @Nullable UUID relationId) {
    return userRelationRepository.myRequests(userId, relationId);
  }

  @Transactional(readOnly = true)
  public List<FriendView> getIncomingRequests(UUID userId, @Nullable UUID relationId) {
    return userRelationRepository.incomingRequests(userId, relationId);
  }

  @Transactional(readOnly = true)
  public List<UserRelationStatusView> getRelationsWithUsers(UUID userId, List<UUID> targetUserIds) {
    if (targetUserIds == null || targetUserIds.isEmpty()) {
      return List.of();
    }
    return userRelationRepository.findRelationsWithUsers(userId, targetUserIds);
  }

  @Transactional(readOnly = true)
  public UserRelationStatusView getRelationWithUser(UUID userId, UUID targetId) {
    List<UserRelationStatusView> list = getRelationsWithUsers(userId, List.of(targetId));
    return list.isEmpty() ? null : list.get(0);
  }
}

