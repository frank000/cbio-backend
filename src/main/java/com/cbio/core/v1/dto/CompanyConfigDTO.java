package com.cbio.core.v1.dto;

import com.cbio.app.entities.CompanyConfigEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyConfigDTO implements Serializable {

    private String id;

    private Boolean keepSameAttendant;

    private String emailCalendar;

    private String companyId;

    private CompanyConfigEntity.GoogleCredentialDTO googleCredential;
}
