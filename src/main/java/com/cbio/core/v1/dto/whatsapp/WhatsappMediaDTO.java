package com.cbio.core.v1.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WhatsappMediaDTO implements Serializable {

    @JsonProperty("sha256") String sha256;

    @JsonProperty("mime_type") String mimeType;

    @JsonProperty("caption") String caption;

    @JsonProperty("id") String id;

    @JsonProperty("type") String type;
}