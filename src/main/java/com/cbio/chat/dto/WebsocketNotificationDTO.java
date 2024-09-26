package com.cbio.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class WebsocketNotificationDTO implements Serializable {

    private String userId;

    private String channelId;

    private String name;

    private String cpf;

    private String nameCanal;

    private String  identificadorRemetente;

    private String path;

    private String time;

    private String preview;

    private List<String> messages;

    private boolean active;

}