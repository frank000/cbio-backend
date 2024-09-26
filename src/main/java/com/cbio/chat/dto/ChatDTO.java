package com.cbio.chat.dto;

import com.cbio.core.v1.dto.MediaDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class ChatDTO implements Serializable {
    private String fromUserId;
    private String toUserId;
    private String text;
    private String time;
    private MediaDTO media;
    private String type;
    private String id;

}
