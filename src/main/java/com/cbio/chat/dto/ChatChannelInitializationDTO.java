package com.cbio.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatChannelInitializationDTO {
  private String userIdOne;

  //Representa a Sessão do usuário
  private String userIdTwo;

  private String initCanal;
}
