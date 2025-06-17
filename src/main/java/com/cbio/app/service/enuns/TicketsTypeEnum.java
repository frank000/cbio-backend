package com.cbio.app.service.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TicketsTypeEnum {
    SUGESTAO(Boolean.TRUE, "Sugestão"),
    RAG(Boolean.FALSE, "RAG"),
    FALHA(Boolean.TRUE, "Falha no sistema"),
    DUVIDA(Boolean.TRUE, "Duvida");

    private Boolean isUser;
    private String title;
}
