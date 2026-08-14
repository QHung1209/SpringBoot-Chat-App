package com.mcxx.chat.user.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserBasicInfo {
  private UUID id;
  private String username;
  private String firstName;
  private String lastName;
  private String fullName;
  private String email;
  private String avatar;

  public String getFullName() {
    if (fullName != null) return fullName;
    if (firstName == null && lastName == null) return null;
    if (firstName == null) return lastName;
    if (lastName == null) return firstName;
    return (firstName + " " + lastName).trim();
  }
}
