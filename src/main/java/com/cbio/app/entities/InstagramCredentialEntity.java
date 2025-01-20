package com.cbio.app.entities;

import com.cbio.app.client.Oauth2Client;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("instagramCredential")
@Data
@Builder
public class InstagramCredentialEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String companyId;

    private Oauth2Client.TokenResponseDTO credential;

    private LocalDateTime createdTime;

    private LocalDateTime expirateTime;
}
