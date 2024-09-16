package com.cbio.app.service.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CanalSenderEnum {

    TELEGRAM("telegramSenderService"),
    WHATSAPP("whatsappSenderService"),;

    private String canalSender;
}
