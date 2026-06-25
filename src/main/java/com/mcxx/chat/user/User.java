package com.mcxx.chat.user;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.hibernate.annotations.UpdateTimestamp;

import com.github.f4b6a3.uuid.UuidCreator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@SoftDelete(columnName = "deleted", strategy = SoftDeleteType.DELETED)
@Getter
@Setter
public class User {
  @Id
  private UUID id;

  @Column(unique = true, nullable = false, length = 255)
  private String username;

  @Column(nullable = false, length = 255)
  private String password;

  @Column(nullable = false, length = 255)
  private String fullName;

  @Column(unique = true, nullable = false, length = 255)
  private String email;

  @Column(unique = true, length = 20)
  private String phoneNumber;

  @Column()
  private String avatarUrl;

  @Column()
  private String bio;

  @Column()
  private Instant lastLogin;

  @CreationTimestamp
  @Column()
  private Instant createdAt;

  @UpdateTimestamp
  @Column()
  private Instant updatedAt;

  @PrePersist
  public void pre() {
    if (id == null) {
      id = UuidCreator.getTimeOrderedEpoch();
    }
  }
}
