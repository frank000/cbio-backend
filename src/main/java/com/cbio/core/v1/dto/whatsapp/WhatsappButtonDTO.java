package com.cbio.core.v1.dto.whatsapp;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WhatsappButtonDTO implements Serializable {


    @Builder.Default
    private String type = "reply";

    private WhatsappReplyDTO reply;
}
