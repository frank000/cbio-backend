package com.cbio.chat.models;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Builder
@Document(collection = "userChat")
public class UserChatEntity {

  @Id
  private String id;
//
//  @NotNull(message="valid email required")
//  @Email(message = "valid email required")
//  private String email;

  private String uuid;

  @NotNull(message="valid name required")
  private String username;

  private String role;

  private Boolean isPresent;

}