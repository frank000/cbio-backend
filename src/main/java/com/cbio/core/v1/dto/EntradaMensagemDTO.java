package com.cbio.core.v1.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EntradaMensagemDTO {

    private String mensagem;
    private CanalDTO canal;
    private String identificadorRemetente;

}
