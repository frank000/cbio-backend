package com.cbio.core.v1.dto;

import com.cbio.app.entities.CompanyConfigEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyConfigDTO implements Serializable {

    private String id;

    private Boolean keepSameAttendant;

    private Boolean autoSend;

    private Boolean isScheduler;

    private String msgNotScheduler;

    private String emailCalendar;

    private String companyId;

    private String model;

    private List<String> rag;

    private CompanyConfigEntity.GoogleCredentialDTO googleCredential;
}
