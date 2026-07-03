package com.mcxx.chat.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mcxx.chat.user.dto.UserBasicInfo;

public interface UserRepository extends JpaRepository<User, UUID> {
  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  Optional<User> findByUsername(String username);

  @Query(value = """
      SELECT u.id as userId, u.full_name, u.avatar_url, u.bio
        FROM users u
        WHERE (:search IS NULL
          OR u.full_name ILIKE CONCAT('%', :search, '%')
          OR u.email ILIKE CONCAT('%', :search, '%'))
        AND (:id IS NULL OR u.id < :id)
        ORDER BY u.id DESC
        LIMIT 30
      """, nativeQuery = true)
  List<UserBasicInfo> searchUsers(String search, UUID id);
}
