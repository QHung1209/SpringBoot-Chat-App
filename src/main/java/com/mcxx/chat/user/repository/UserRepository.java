package com.mcxx.chat.user.repository;

import com.mcxx.chat.user.domain.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mcxx.chat.user.dto.response.UserSearchView;

public interface UserRepository extends JpaRepository<User, UUID> {
  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  Optional<User> findByUsername(String username);

  @Query(value = """
      SELECT
        u.id AS id,
        u.username AS username,
        u.full_name AS "fullName",
        u.email AS email,
        u.avatar_url AS "avatarUrl",
        u.bio AS bio,
        r.status AS "relationStatus",
        r.id AS "relationId",
        r.action_user_id AS "actionUserId"
        FROM users u
        LEFT JOIN user_relations r
          ON (
            (r.user_low_id = :currentUserId AND r.user_high_id = u.id)
            OR (r.user_low_id = u.id AND r.user_high_id = :currentUserId)
          )
        WHERE u.deleted = false
        AND u.id <> :currentUserId
        AND (cast(:search as text) IS NULL
          OR u.full_name ILIKE CONCAT('%', :search, '%')
          OR u.username ILIKE CONCAT('%', :search, '%')
          OR u.email ILIKE CONCAT('%', :search, '%'))
        AND (cast(:cursor as uuid) IS NULL OR u.id < cast(:cursor as uuid))
        ORDER BY u.id DESC
        LIMIT 30
      """, nativeQuery = true)
  List<UserSearchView> searchUsers(UUID currentUserId, String search, UUID cursor);
}
