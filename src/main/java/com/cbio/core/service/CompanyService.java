package com.cbio.core.service;

import com.cbio.app.entities.StatusPaymentEnum;
import com.cbio.app.exception.CbioException;
import com.cbio.core.v1.dto.CompanyConfigDTO;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.InstagramCredentialDTO;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CompanyService {

    CompanyDTO save(CompanyDTO companyDTO) throws CbioException;

    CompanyDTO edit(CompanyDTO companyDTO);

    void changeStatusPayment(String companyId, StatusPaymentEnum statusPayment) throws CbioException;

    Optional<StatusPaymentEnum> getStatusPayment(String companyId);

    void toggleSchedulingToCompany() throws IOException;

    Boolean isCompanyScheduler() throws IOException;

    void delete(String id);

    CompanyDTO findById(String id);

    Integer getNumAttendantsToCompany(String id);

    List<CompanyDTO> findAll();

    Integer getFreePort();

    Integer getPortByIdCompany(String id);

    CompanyConfigDTO saveConfigCompany(CompanyConfigDTO dto) throws CbioException, IOException, InterruptedException;

    CompanyConfigDTO getConfigCompany(String id) throws CbioException;

    Boolean hasGoogleCrendential();
    StatusPaymentEnum getStatusPayment();

    Boolean hasGoogleCrendential(String id);

    InstagramCredentialDTO getCredentialInstagram(String id);

    CompanyConfigDTO fetchOrCreateConfigPreferencesCompany(String id) throws CbioException;

    void completeProfile(String id, String pass) throws MessagingException;
}
