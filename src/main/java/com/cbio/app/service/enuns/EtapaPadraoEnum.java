package com.cbio.app.service.enuns;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EtapaPadraoEnum {

    INIT("init"),
    END("end");

    String valor;

    EtapaPadraoEnum(String valor) {
        this.valor = valor;
    }
}
