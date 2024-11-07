package com.cbio.core.v1.dto.google;

import com.google.api.client.json.JsonFactory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CredentialData {
    private String accessToken;
    private String refreshToken;
    private Long expirationTimeMillis;
    private JsonFactory jsonFactory;
    // Construtores, getters e setters
}
