package com.cbio.core.service;

import com.cbio.core.v1.dto.CompanyConfigDTO;

public interface StoriesService {
    void addSchedulingStories(CompanyConfigDTO dto);
    void removeSchedulingStories(CompanyConfigDTO dto);

}
