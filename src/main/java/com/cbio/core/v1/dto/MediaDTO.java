package com.cbio.core.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaDTO implements MediaInterface, Serializable {

    @JsonProperty("sha256")
    private String sha;
    private String mimeType;
    private String caption;
    private String id;
    private String url;
    private String mediaType;
}
