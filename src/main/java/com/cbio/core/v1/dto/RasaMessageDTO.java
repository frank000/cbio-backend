package com.cbio.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class RasaMessageDTO implements Serializable {

    @JsonProperty("recipient_id")
    private String identificadorId;

    @JsonProperty("text")
    private String text;

    private List<Button> buttons;



    @Getter
    @Setter
    public static class Button  implements Serializable{
        @JsonProperty("title")
        private String title;

        @JsonProperty("payload")
        private String payload;

    }
}