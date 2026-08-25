package com.mcxx.chat.userrelation.repository;

import com.mcxx.chat.userrelation.domain.UserRelation;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.mcxx.chat.userrelation.dto.response.FriendView;
import com.mcxx.chat.userrelation.dto.response.UserRelationStatusView;

public interface UserRelationRepository extends JpaRepository<UserRelation, UUID> {

  @Query(
      value = """
          SELECT u.id as userId, u.first_name as firstName, u.last_name as lastName, u.avatar_url, u.bio, f.relationId
          FROM users u
          JOIN (
            SELECT
              CASE
                WHEN r.user_low_id = :id THEN r.user_high_id
                ELSE r.user_low_id
              END as userId,
              r.id as relationId
            FROM user_relations r
            JOIN users u
              ON u.id = CASE
                          WHEN r.user_low_id = :id THEN r.user_high_id
                          ELSE r.user_low_id
                        END
            WHERE (cast(:search as text) IS NULL OR u.first_name ILIKE CONCAT('%', :search, '%') OR u.last_name ILIKE CONCAT('%', :search, '%'))
            AND r.status = :status
            AND (r.user_low_id = :id OR r.user_high_id = :id)
            AND (cast(:relationId as uuid) IS NULL OR r.id < cast(:relationId as uuid))

            ORDER BY r.id DESC
            LIMIT 30
          ) f

          ON u.id = f.userId

          ORDER BY f.relationId DESC
          """,
      nativeQuery = true)
  List<FriendView> findRelations(UUID id, UUID relationId, String search, String status);

  @Query(value = """
      SELECT CASE
        WHEN r.user_low_id = :userId THEN r.user_high_id
        ELSE r.user_low_id
      END
      FROM user_relations r
      WHERE (r.user_low_id = :userId OR r.user_high_id = :userId)
        AND r.status = 'ACCEPTED'
      """, nativeQuery = true)
  List<UUID> findFriendIds(UUID userId);


  @Modifying
  @Query("""
      UPDATE UserRelation ur
      SET ur.status = 'BLOCKED'
      WHERE ur.userLowId = :lowerId AND ur.userHighId = :higherId
      """)
  void blockUser(UUID lowerId, UUID higherId);

  @Modifying
  void deleteByUserLowIdAndUserHighId(UUID lowerId, UUID higherId);

  @Modifying
  @Query("""
      UPDATE UserRelation ur
      SET ur.status = 'ACCEPTED'
      WHERE ur.userLowId = :lowerId AND ur.userHighId = :higherId
      """)
  void acceptUser(UUID lowerId, UUID higherId);

  @Query(value = """
      SELECT
            u.id as userId,
            u.first_name as firstName,
            u.last_name as lastName,
            u.avatar_url,
            u.bio,
            f.relationId
      FROM users u
      JOIN (
      SELECT
        CASE
          WHEN r.user_low_id = :id THEN r.user_high_id
          ELSE r.user_low_id
        END as userId, r.id as relationId
      FROM user_relations r
      WHERE r.status = 'PENDING'
        AND r.action_user_id = :id
      AND (cast(:relationId as uuid) IS NULL OR r.id < cast(:relationId as uuid))
      ORDER BY r.id DESC
      LIMIT 30
      ) f

      ON u.id = f.userId
      ORDER BY f.relationId DESC
      """, nativeQuery = true)
  List<FriendView> myRequests(UUID id, UUID relationId);

  @Query(value = """
      SELECT
            u.id as userId,
            u.first_name as firstName,
            u.last_name as lastName,
            u.avatar_url,
            u.bio,
            f.relationId
      FROM users u
      JOIN (
      SELECT
        CASE
          WHEN r.user_low_id = :id THEN r.user_high_id
          ELSE r.user_low_id
        END as userId, r.id as relationId
      FROM user_relations r
      WHERE r.status = 'PENDING'
        AND (r.user_low_id = :id OR r.user_high_id = :id)
        AND r.action_user_id <> :id
      AND (cast(:relationId as uuid) IS NULL OR r.id < cast(:relationId as uuid))
      ORDER BY r.id DESC
      LIMIT 30
      ) f

      ON u.id = f.userId
      ORDER BY f.relationId DESC
      """, nativeQuery = true)
  List<FriendView> incomingRequests(UUID id, UUID relationId);

  @Query(value = """
      SELECT
        CASE
          WHEN r.user_low_id = :userId THEN r.user_high_id
          ELSE r.user_low_id
        END AS "targetUserId",
        r.status AS "status",
        r.action_user_id AS "actionUserId",
        r.id AS "relationId"
      FROM user_relations r
      WHERE (r.user_low_id = :userId AND r.user_high_id IN (:targetUserIds))
         OR (r.user_high_id = :userId AND r.user_low_id IN (:targetUserIds))
      """, nativeQuery = true)
  List<UserRelationStatusView> findRelationsWithUsers(UUID userId, List<UUID> targetUserIds);

  java.util.Optional<UserRelation> findByUserLowIdAndUserHighId(UUID userLowId, UUID userHighId);
}
