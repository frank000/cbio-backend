package com.cbio.app.service.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TicketsTypeEnum {
    SUGESTAO(Boolean.TRUE, "Sugestão"),
    RAG(Boolean.FALSE, "RAG"),
    DUVIDA(Boolean.TRUE, "Duvida");

    private Boolean isUser;
    private String title;
}
