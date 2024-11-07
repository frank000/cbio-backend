package com.cbio.chat.dto;

import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.MediaDTO;
import com.cbio.core.v1.dto.ModelDTO;
import com.cbio.core.v1.dto.RasaMessageDTO;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class DialogoDTO implements Serializable {
    private String id;
    private String uuid;
    private String mensagem;
    private MediaDTO media;
    private String identificadorRemetente;//NA VERDADE É O DESTINATÁRIO todo ANALASYS THE REFACTOR
    private String sessionId;
    private String type;
    private String toIdentifier;
    private CanalDTO canal;
    private String channelUuid;
    private String from;
    private LocalDateTime createdDateTime;
    private ModelDTO model;

    @Getter(AccessLevel.NONE)
    List<RasaMessageDTO.Button> buttons;

    @Getter(AccessLevel.NONE)
    private Map<String,Object> variables;

    public Map<String, Object> getVariables() {
        if(variables == null){
            variables = new HashMap<>();
        }
        return variables;
    }

    public List<RasaMessageDTO.Button> getButtons() {
        if (buttons == null) {
            buttons = new ArrayList<>();
        }
        return buttons;
    }

    public String getOnlyIdentificadorRementente() {
        if (identificadorRemetente == null) {
            return null;
        } else {
            String[] splits = identificadorRemetente.split("_");
            return splits.length > 1 ? splits[0] : identificadorRemetente;
        }
    }

    public static enum TypeMessageEnum {
        DOCUMENT,
        IMAGE,
        TEXT,
        MODEL;
    }
}
