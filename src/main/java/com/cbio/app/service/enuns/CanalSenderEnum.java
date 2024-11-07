package com.cbio.app.service.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CanalSenderEnum {

    TELEGRAM("telegram","telegramSenderService"),
    WHATSAPP("whatsapp","whatsappSenderService"),;

    private String title;
    private String canalSender;
}
