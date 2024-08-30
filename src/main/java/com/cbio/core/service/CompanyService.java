package com.cbio.core.service;

import com.cbio.core.v1.dto.CompanyDTO;

public interface CompanyService {

    CompanyDTO save(CompanyDTO companyDTO);

    CompanyDTO findById(String id);

}
