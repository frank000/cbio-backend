package com.cbio.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class RasaMessageInDTO implements Serializable {

    @JsonProperty("text")
    private String mensagem;

    @JsonProperty("recipient_id")
    private String identificadorRemetente;
}
