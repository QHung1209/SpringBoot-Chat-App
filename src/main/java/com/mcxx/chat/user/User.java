package com.mcxx.chat.user;

import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import com.mcxx.chat.common.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@SoftDelete(columnName = "deleted", strategy = SoftDeleteType.DELETED)
@Getter
@Setter
public class User extends BaseEntity {
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

}
