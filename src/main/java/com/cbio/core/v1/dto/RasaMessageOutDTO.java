package com.cbio.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class RasaMessageOutDTO implements Serializable {

    @JsonProperty("message")
    private String mensagem;

    @JsonProperty("sender")
    private String identificadorRemetente;
}
