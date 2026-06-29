package com.mcxx.chat.auth.device;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {
  void deleteByUserId(UUID userId);

  List<UserDevice> findByUserId(UUID userId);

  @Modifying
  @Query("""
      Update UserDevice d
      Set d.tokenVersion = d.tokenVersion + 1
      where d.userId = :userId
      """)
  void incrementTokenVersionByUserId(@Param("userId") UUID userId);

  Optional<UserDevice> findByIdAndUserId(UUID deviceId, UUID userId);

  List<UserDevice> findByUserIdOrderByCreatedAtDesc(UUID userId);

}
