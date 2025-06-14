package com.cbio.core.v1.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class    EventDTO {

    @JsonProperty("previous_attributes")
    private Object previousAttributes;

    @JsonProperty("object")
    private EventSessionDTO checkoutSession;
}