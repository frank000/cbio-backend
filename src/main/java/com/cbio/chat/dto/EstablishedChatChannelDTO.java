package com.cbio.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class EstablishedChatChannelDTO {
  private String channelUuid;
  
  private String userOneFullName;
  
  private String userTwoFullName;

}