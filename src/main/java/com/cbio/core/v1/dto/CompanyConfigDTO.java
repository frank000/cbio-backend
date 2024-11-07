package com.cbio.core.v1.dto;

import com.cbio.app.entities.CompanyConfigEntity;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class CompanyConfigDTO implements Serializable {

    private String id;

    private Boolean keepSameAttendant;

    private String emailCalendar;

    private String companyId;

    private CompanyConfigEntity.GoogleCredentialDTO googleCredential;
}
