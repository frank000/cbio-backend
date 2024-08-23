package com.cbio.chat.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageDTO {
  private String contents;

  private String fromUserId;
  
  private String toUserId;


}
