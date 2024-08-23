package com.cbio.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
  private String id;

  private String email;

  private String fullName;

  public UserDTO() {}

  public UserDTO(String id, String fullName) {
    this.id = id;
    this.fullName = fullName;
  }


}