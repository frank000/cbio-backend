package com.cbio.app.service.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusTicketsEnum {
    NOVO("Novo"),
    EM_ATENDIMENTO("Em Atendimento"),
    FECHADO("Fechado");

    private String label;
}
