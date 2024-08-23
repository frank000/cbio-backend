package com.cbio.chat.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@Document(collection = "chatMessage")
public class ChatMessageEntity {
  @Id
  private long id;

  @DBRef
  private UserChatEntity authorUser;

  @DBRef
  private UserChatEntity recipientUser;

  private Date timeSent;

  private String contents;

  public ChatMessageEntity() {}

  public ChatMessageEntity(UserChatEntity authorUser, UserChatEntity recipientUser, String contents) {
    this.authorUser = authorUser;
    this.recipientUser = recipientUser;
    this.contents = contents;
    this.timeSent = new Date();
  }


}