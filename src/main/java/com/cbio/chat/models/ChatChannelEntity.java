package com.cbio.chat.models;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "chatChannel")
public class ChatChannelEntity {

  @Id
  private String id;


  private UserChatEntity userOne;


  private UserChatEntity userTwo;

  private String initCanal;


  public ChatChannelEntity(UserChatEntity userOne , UserChatEntity userTwo) {
    this.userOne = userOne;
    this.userTwo = userTwo;
  }
  public ChatChannelEntity(UserChatEntity userOne , UserChatEntity userTwo, String initCanal) {
    this.userOne = userOne;
    this.userTwo = userTwo;
    this.initCanal = initCanal;
  }
  public ChatChannelEntity(String id, UserChatEntity userOne , UserChatEntity userTwo) {
    this.id = id;
    this.userOne = userOne;
    this.userTwo = userTwo;
  }




}