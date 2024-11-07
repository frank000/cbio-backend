package com.cbio.app.entities;

import com.cbio.core.v1.dto.google.CredentialData;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("googleCredential")
@Data
@Builder
public class GoogleCredentialEntity {

    @Id
    private String id;

    @Indexed
    private String userId;

    private CredentialData credential;

    private LocalDateTime createdTime;
}
