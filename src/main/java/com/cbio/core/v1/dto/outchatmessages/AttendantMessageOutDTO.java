package com.cbio.core.v1.dto.outchatmessages;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class AttendantMessageOutDTO implements Serializable {

    @JsonProperty("text")
    private String text;

    @JsonProperty("fromUserId")
    private String fromUserId;

    @JsonProperty("toUserId")
    private String toUserId;

    @JsonProperty("time")
    private String time;
}

