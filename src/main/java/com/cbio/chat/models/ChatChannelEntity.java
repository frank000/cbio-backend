package com.cbio.chat.models;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document("chatChannel")
public class ChatChannelEntity {

  @Id
  private String id;


  private UserChatEntity userOne;


  private UserChatEntity userTwo;

  private String initCanal;

  private LocalDateTime initTime;


  private List<LocalDateTime> history;

  public void addHistory(LocalDateTime history) {
    if (this.history == null) {
      this.history = new ArrayList<>();
    }
    this.history.add(history);
  }

  public ChatChannelEntity(UserChatEntity userOne , UserChatEntity userTwo) {
    this.userOne = userOne;
    this.userTwo = userTwo;
  }
  public ChatChannelEntity(UserChatEntity userOne , UserChatEntity userTwo, String initCanal) {
    this.userOne = userOne;
    this.userTwo = userTwo;
    this.initCanal = initCanal;
  }
  public ChatChannelEntity(UserChatEntity userOne , UserChatEntity userTwo, String initCanal,  LocalDateTime initTime) {
    this.userOne = userOne;
    this.userTwo = userTwo;
    this.initCanal = initCanal;
    this.initTime = initTime;
  }
  public ChatChannelEntity(String id, UserChatEntity userOne , UserChatEntity userTwo) {
    this.id = id;
    this.userOne = userOne;
    this.userTwo = userTwo;
  }




}