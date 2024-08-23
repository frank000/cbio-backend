package com.cbio.chat.dto;

public class RegistrationDTO {
  private String email;
  private String fullName;

  public RegistrationDTO() {}

  public RegistrationDTO(String email, String fullName ) {
    this.email = email;
    this.fullName = fullName; 
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEmail() {
    return this.email;
  }
}