package com.cbio.core.service;

import com.cbio.core.v1.dto.ResourceDTO;

import java.util.List;
import java.util.Optional;

public interface ResourceService {

    ResourceDTO save(ResourceDTO dto);

    ResourceDTO update(ResourceDTO dto);

    ResourceDTO getResourceById(String resourceId);

    void notifyByConfigNotification();

    void delete(String id);

    List<ResourceDTO> getResourceFilterSelection();

    Optional<ResourceDTO> getResourceByCompanyAndDairyName(String dairyName);

    Optional<ResourceDTO> getResourceByCompanyAndDairyNameAndCompanyId(String dairyName, String companyId);

    void saveResourceByDairyName(String dairyName, String companyId);

    List<ResourceDTO> getAllResourcesIsConfigured();
}
