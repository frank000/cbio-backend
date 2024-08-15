package com.policia.df.bot.core.v1.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class DialogoDTO implements Serializable {
    private String mensagem;
    private String identificadorRemetente;
    private CanalDTO canal;
}
