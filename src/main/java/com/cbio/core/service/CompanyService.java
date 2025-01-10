package com.cbio.core.service;

import com.cbio.app.exception.CbioException;
import com.cbio.core.v1.dto.CompanyConfigDTO;
import com.cbio.core.v1.dto.CompanyDTO;

import java.io.IOException;
import java.util.List;

public interface CompanyService {

    CompanyDTO save(CompanyDTO companyDTO) throws CbioException;

    CompanyDTO edit(CompanyDTO companyDTO);

    void delete(String id);

    CompanyDTO findById(String id);

    Integer getNumAttendantsToCompany(String id);

    List<CompanyDTO> findAll();

    Integer getFreePort();

    Integer getPortByIdCompany(String id);

    CompanyConfigDTO saveConfigCompany(CompanyConfigDTO dto) throws CbioException, IOException, InterruptedException;

    CompanyConfigDTO getConfigCompany(String id) throws CbioException;

    Boolean hasGoogleCrendential();

    Boolean hasGoogleCrendential(String id);

    CompanyConfigDTO fetchOrCreateConfigPreferencesCompany(String id) throws CbioException;
}
