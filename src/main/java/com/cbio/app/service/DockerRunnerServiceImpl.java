package com.cbio.app.service;

import com.cbio.core.service.CompanyService;
import com.cbio.core.v1.dto.CompanyDTO;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Service
public class DockerRunnerServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(DockerRunnerServiceImpl.class);
    private final DockerServiceImpl dockerService;

    private final CompanyService companyService;

    @PostConstruct
    public void initContainerDowned() {
        try {
            List<CompanyDTO> companyDTOList = companyService.findAll();
            companyDTOList
                    .forEach(companyDTO -> {
                        String image = String.format(DockerServiceImpl.IMAGE_INIT_NAME, companyDTO.getId());
                        String container = String.format(DockerServiceImpl.CONTAINER_INIT_NAME, image);
                        if(!dockerService.isContainerRunning(container)){
                            try {
                                dockerService.executeCompleteDockerFlow(companyDTO.getId(), String.valueOf(companyDTO.getPorta()));
                            } catch (IOException | InterruptedException e) {
                                e.getMessage();
                            }
                        }else{
                            log.info("Container {} already running.", container);
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException("Falha ao iniciar container: " + e.getMessage(), e);
        }
    }


}