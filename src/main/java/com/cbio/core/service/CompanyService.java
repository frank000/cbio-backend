package com.cbio.core.service;

import com.cbio.app.exception.CbioException;
import com.cbio.core.v1.dto.CompanyConfigDTO;
import com.cbio.core.v1.dto.CompanyDTO;

public interface CompanyService {

    CompanyDTO save(CompanyDTO companyDTO) throws CbioException;

    CompanyDTO edit(CompanyDTO companyDTO);

    void delete(String id);

    CompanyDTO findById(String id);

    Integer getNumAttendantsToCompany(String id);

    Integer getFreePort();

    Integer getPortByIdCompany(String id);

    CompanyConfigDTO saveConfigCompany(CompanyConfigDTO dto) throws CbioException;

    CompanyConfigDTO getConfigCompany(String id) throws CbioException;

    Boolean hasGoogleCrendential();

    Boolean hasGoogleCrendential(String id);
}
