package com.cbio.core.v1.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class DialogoDTO implements Serializable {
    private String mensagem;
    private String identificadorRemetente;
    private CanalDTO canal;

    @Getter(AccessLevel.NONE)
    List<RasaMessageDTO.Button> buttons;

    public List<RasaMessageDTO.Button> getButtons() {
        if (buttons == null) {
            buttons = new ArrayList<>();
        }
        return buttons;
    }
}
