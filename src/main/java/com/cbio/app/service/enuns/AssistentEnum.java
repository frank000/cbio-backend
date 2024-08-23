package com.cbio.app.service.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AssistentEnum {
    RASA("rasaAssistent"),
    ATTENDANT("attendantAssistent");

    private String beanName;
}
