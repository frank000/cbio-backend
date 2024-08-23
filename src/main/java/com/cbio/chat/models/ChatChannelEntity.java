package com.cbio.chat.models;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "chatChannel")
public class ChatChannelEntity {

  @Id
  private String id;


  private UserChatEntity userOne;


  private UserChatEntity userTwo;

  public ChatChannelEntity(UserChatEntity userOne , UserChatEntity userTwo) {
    this.userOne = userOne;
    this.userTwo = userTwo;
  }
  public ChatChannelEntity(String id, UserChatEntity userOne , UserChatEntity userTwo) {
    this.id = id;
    this.userOne = userOne;
    this.userTwo = userTwo;
  }




}