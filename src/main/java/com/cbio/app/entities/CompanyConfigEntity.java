package com.cbio.app.entities;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Document("companyConfig")
@Getter
@Setter
@Builder
public class CompanyConfigEntity {

    @Id
    private String id;

    @Getter(AccessLevel.NONE)
    private Boolean keepSameAttendant;

    @Getter(AccessLevel.NONE)
    private Boolean autoSend;

    private String emailCalendar;

    private String model;

    @Indexed(unique = true)
    private String companyId;

    private GoogleCredentialDTO googleCredential;

    @Getter(AccessLevel.NONE)
    private List<String> rag;

    public List<String> getRag() {
        if(rag == null) {
            rag = new ArrayList<>();
        }
        return rag;
    }

    public boolean getKeepSameAttendant() {
        if (keepSameAttendant == null) {
            keepSameAttendant = Boolean.FALSE;
        }
        return keepSameAttendant;
    }

    public Boolean getAutoSend() {
        if (autoSend == null) {
            autoSend = Boolean.FALSE;
        }
        return autoSend;
    }

    @Getter
    @Setter
    @Builder
    public static class GoogleCredentialDTO implements Serializable {
        private String clientId;
        private String clientSecret;
    }

}
