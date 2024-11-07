package com.cbio.app.entities;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document("companyConfig")
@Getter
@Setter
@Builder
public class CompanyConfigEntity {

    @Id
    private String id;

    @Getter(AccessLevel.NONE)
    private Boolean keepSameAttendant;

    private String emailCalendar;


    @Indexed
    private String companyId;

    private GoogleCredentialDTO googleCredential;


    public boolean getKeepSameAttendant() {
        if (keepSameAttendant == null) {
            keepSameAttendant = Boolean.FALSE;
        }
        return keepSameAttendant;
    }

    @Getter
    @Setter
    @Builder
    public static class GoogleCredentialDTO implements Serializable {
        private String clientId;
        private String clientSecret;
    }

}
