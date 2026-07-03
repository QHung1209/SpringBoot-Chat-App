package com.mcxx.chat.userrelation;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.mcxx.chat.userrelation.dto.FriendView;

public interface UserRelationRepository extends JpaRepository<UserRelation, UUID> {

  @Query(value = """
      SELECT u.id as userId, u.full_name, u.avatar_url, u.bio, f.relationId
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
        WHERE (:search IS NULL OR u.full_name ILIKE CONCAT('%', :search, '%'))
        AND r.status = :status
        AND (r.user_low_id = :id OR r.user_high_id = :id)
        AND (:relationId IS NULL OR r.id < :relationId)

        ORDER BY r.id DESC
        LIMIT 30
      ) f


      ON u.id = f.userId

      ORDER BY f.relationId DESC
      """, nativeQuery = true)
  List<FriendView> findRelations(UUID id, UUID relationId, String search, String status);

  @Modifying
  @Query(
      value = """
          UPDATE user_relations  SET status = 'BLOCKED' WHERE user_low_id = :lowerId AND user_high_id = :higherId
          """,
      nativeQuery = true)
  void blockUser(UUID lowerId, UUID higherId);

  @Modifying
  @Query(value = """
      DELETE FROM user_relations  WHERE user_low_id = :lowerId AND user_high_id = :higherId
      """, nativeQuery = true)
  void deleteRelation(UUID lowerId, UUID higherId);

  @Modifying
  @Query(
      value = """
          UPDATE user_relations SET status = 'ACCEPTED' WHERE user_low_id = :lowerId AND user_high_id = :higherId
          """,
      nativeQuery = true)
  void acceptUser(UUID lowerId, UUID higherId);

  @Query(value = """
      SELECT u.id as userId, u.full_name, u.avatar_url, u.bio, f.relationId
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
      AND (:relationId IS NULL OR r.id < :relationId)
      ORDER BY r.id DESC
      LIMIT 30
      ) f

      ON u.id = f.userId
      ORDER BY f.relationId DESC
      """, nativeQuery = true)
  List<FriendView> myRequests(UUID id, UUID relationId);
}
