package com.cbio.app.service;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.*;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.CompanyConfigRepository;
import com.cbio.app.repository.CompanyRepository;
import com.cbio.app.repository.GoogleCredentialRepository;
import com.cbio.app.repository.InstagramCredentialRepository;
import com.cbio.app.service.enuns.TicketsTypeEnum;
import com.cbio.app.service.mapper.CompanyConfigMapper;
import com.cbio.app.service.mapper.CompanyMapper;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.CompanyService;
import com.cbio.core.service.TicketService;
import com.cbio.core.v1.dto.CompanyConfigDTO;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.InstagramCredentialDTO;
import com.cbio.core.v1.dto.TicketDTO;
import com.cbio.ia.service.OpenAIService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CompanyServiceImpl implements CompanyService {

    public static final String INTENT_NAME_FAQ = "faq";
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final CompanyConfigMapper companyConfigMapper;
    private final CompanyConfigRepository companyConfigRepository;
    private final GoogleCredentialRepository googleCredentialRepository;
    private final AuthService authService;
    private final TicketService ticketService;
    private final DockerServiceImpl dockerService;

    private final DirectoryRasaServiceImpl directoryRasaService;
    private final OpenAIService openAIService;
    private final NluYamlManagerServiceImpl nluYamlManagerServiceImpl;
    private final DockerServiceImpl dockerServiceImpl;
    private final InstagramCredentialRepository instagramCredentialRepository;


    @Value("${app.rasa.targe-path}")
    public String BASE_TARGET_DIR;


    public CompanyDTO save(CompanyDTO companyDTO) throws CbioException {
        try {
            CompanyEntity entity = companyMapper.toEntity(companyDTO);
            CompanyEntity save = companyRepository.save(entity);

            CompanyConfigDTO configDTO = CompanyConfigDTO.builder()
                    .companyId(save.getId())
                    .emailCalendar(save.getEmail())
                    .build();

            saveConfigCompany(configDTO);

            directoryRasaService.copyRasaProject(save.getId());

            dockerService.buildDockerImageAndRunContainer(save.getId(), String.valueOf(save.getPorta()));


            return companyMapper.toDto(save);

        } catch (IOException e) {
            throw new RuntimeException("Problema na geração da pasta default do chatbot");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CompanyDTO findById(String id) {
        CompanyEntity entity = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Companhia não encontrada"));
        return companyMapper.toDto(entity);
    }

    public List<CompanyDTO> findAll() {
        return companyMapper.toDto(companyRepository.findAll());
    }


    @Override
    public void delete(String id) {
        companyRepository.findById(id)
                .ifPresent(companyEntity -> {
                    try {
                        directoryRasaService.deleteRasaProject(companyEntity.getId());

                        revokeContainerFromCompany(companyEntity.getId());

                        companyConfigRepository.deleteByCompanyId(companyEntity.getId());
                        companyRepository.delete(companyEntity);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void revokeContainerFromCompany(String id) {

        String imageName = DockerServiceImpl.getImageName(id);
        String containerName = DockerServiceImpl.getContainerName(imageName);
        dockerServiceImpl.stopAndRemoveContainer(containerName);
    }

    @Override
    public Integer getNumAttendantsToCompany(String id) {
        Optional<TierEntity> tierByCompanyId = companyRepository.getTierById(id);
        return tierByCompanyId
                .orElseThrow().getNumAttendants();
    }

    @Override
    public CompanyDTO edit(CompanyDTO companyDTO) {
        CompanyEntity companyEntity = companyRepository.findById(companyDTO.getId())
                .orElseThrow(() -> new RuntimeException("Companhia não encontrada."));

        companyMapper.fromDto(companyDTO, companyEntity);

        return companyMapper.toDto(companyRepository.save(companyEntity));
    }

    public Integer getFreePort() {
        CompanyEntity company = companyRepository.findFirstByOrderByDataCadastroAsc()
                .orElseThrow(() -> new NotFoundException("Nenhum encontrado"));

        return company.getPorta() + 1;
    }

    @Override
    public Integer getPortByIdCompany(String id) {
        return findById(id).getPorta();
    }

    public CompanyConfigDTO saveConfigCompany(CompanyConfigDTO dto) throws CbioException, IOException, InterruptedException {
        CompanyConfigEntity entity;
        boolean updateRagFields = false;
        boolean isNewConfigAndHasRag = false;
        if (StringUtils.hasText(dto.getId())) {
            entity = companyConfigRepository.findById(dto.getId())
                    .orElseThrow(() -> new CbioException("Configuração não encontrada.", HttpStatus.NO_CONTENT.value()));

            isNewConfigAndHasRag = CollectionUtils.isEmpty(entity.getRag()) &&  !CollectionUtils.isEmpty(dto.getRag());
            updateRagFields =  !CollectionUtils.isEmpty(entity.getRag()) && !entity.getRag().get(0).equals(dto.getRag().get(0));



            companyConfigMapper.fromDto(dto, entity);
            entity.setCompanyId(entity.getCompanyId());
        } else {
            entity = CompanyConfigEntity.builder()
                    .rag(dto.getRag())
                    .companyId(dto.getCompanyId())
                    .model(dto.getModel())
                    .autoSend(dto.getAutoSend())
                    .emailCalendar(dto.getEmailCalendar())
                    .keepSameAttendant(dto.getKeepSameAttendant())
                    .googleCredential(dto.getGoogleCredential())
                            .build();

            updateRagFields = true;
        }


        if (updateRagFields || isNewConfigAndHasRag) {
            String collectRag = String.join(" ", dto.getRag());


            if (StringUtils.hasText(collectRag)) {
                String onlyQuestionFromRag = openAIService.getOnlyQuestionFromRag(collectRag);
                fullUpdateNLUFromRasa(dto, onlyQuestionFromRag);
            } else {
                String nluFilePath = BASE_TARGET_DIR
                        .concat(File.separator)
                        .concat(dto.getCompanyId())
                        .concat(File.separator)
                        .concat("data/nlu.yml");

                NluYamlManagerServiceImpl.NluConfig nluConfig = nluYamlManagerServiceImpl.readNluFile(nluFilePath);

                nluYamlManagerServiceImpl.removeIntent(nluConfig, INTENT_NAME_FAQ);
                nluYamlManagerServiceImpl.saveNluFile(nluFilePath, nluConfig);
                runDocker(dto.getCompanyId());
            }
        }

        entity = companyConfigRepository.save(entity);

        return companyConfigMapper.toDto(entity);
    }

    @Async
    protected void fullUpdateNLUFromRasa(CompanyConfigDTO dto, String onlyQuestionFromRag) {
        try {

            String nluFilePath = BASE_TARGET_DIR
                    .concat(File.separator)
                    .concat(dto.getCompanyId())
                    .concat(File.separator)
                    .concat("data/nlu.yml");

            NluYamlManagerServiceImpl.NluConfig nluConfig = nluYamlManagerServiceImpl.readNluFile(nluFilePath);

            nluYamlManagerServiceImpl.removeIntent(nluConfig, INTENT_NAME_FAQ);
            nluYamlManagerServiceImpl.addIntent(nluConfig, INTENT_NAME_FAQ, getExempleList(onlyQuestionFromRag));
            nluYamlManagerServiceImpl.saveNluFile(nluFilePath, nluConfig);

            runDocker(dto.getCompanyId());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        geraTicket(dto);
    }

    private void runDocker(String companyId) throws IOException, InterruptedException {

        Integer portByIdCompany = getPortByIdCompany(companyId);

        String imageName = DockerServiceImpl.getImageName(companyId);
        String containerName = DockerServiceImpl.getContainerName(imageName);

        if (dockerServiceImpl.isContainerRunning(containerName)) {
            dockerServiceImpl.executeCompleteDockerFlow(companyId, String.valueOf(portByIdCompany));
        } else {
            dockerServiceImpl.buildDockerImageAndRunContainer(companyId, String.valueOf(portByIdCompany));
        }
    }

    @NotNull
    private static List<String> getExempleList(String onlyQuestionFromRag) {
        return Arrays.stream(onlyQuestionFromRag.replace("\n", "").split("\\?")).toList();
    }

    private void geraTicket(CompanyConfigDTO dto) {
        List<TicketEntity.TicketMessageDTO> list = new ArrayList<>();

        list.add(
                TicketEntity.TicketMessageDTO.builder()
                        .message(dto.getRag().get(0))
                        .createdAt(CbioDateUtils.LocalDateTimes.now())
                        .build()
        );

        TicketDTO ticketDTO = TicketDTO.builder()
                .title(TicketsTypeEnum.RAG.getTitle())
                .type(TicketsTypeEnum.RAG.name())
                .ticketMessages(list)
                .ativo(Boolean.TRUE)
                .company(CompanyDTO.builder()
                        .id(dto.getCompanyId())
                        .build())
                .createdAt(CbioDateUtils.LocalDateTimes.now())
                .build();

        TicketDTO savedTicket = ticketService.save(ticketDTO);
    }


    @Override
    public CompanyConfigDTO getConfigCompany(String id) throws CbioException {
        CompanyConfigEntity companyConfigEntity = companyConfigRepository.findByCompanyId(id)
                .orElseThrow(() -> new CbioException("Configuração não encontrada.", HttpStatus.NO_CONTENT.value()));
        return companyConfigMapper.toDto(companyConfigEntity);
    }


    public CompanyConfigDTO fetchOrCreateConfigPreferencesCompany(String id) {
        Optional<CompanyConfigEntity> byCompany = companyConfigRepository.getPreferencesByCompany(id);

        CompanyConfigEntity entity = byCompany
                .orElseGet(() -> companyConfigRepository.save(
                        CompanyConfigEntity.builder()
                                .companyId(id)
                                .build()));

        return companyConfigMapper.toDto(entity);
    }

    public Boolean hasGoogleCrendential(String id) {
        if (StringUtils.hasText(id)) {
            Optional<GoogleCredentialEntity> byUserId = googleCredentialRepository.findByUserId(id);
            return byUserId.isEmpty() ? Boolean.FALSE : Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    @Override
    public InstagramCredentialDTO getCredentialInstagram(String id) {

        LocalDateTime today = CbioDateUtils.LocalDateTimes.now();
        Optional<InstagramCredentialEntity> byCompanyId = instagramCredentialRepository.findByCompanyIdAndExpirateTimeIsAfter(id, today);
        return byCompanyId
                .map(instagramCredentialEntity ->
                        InstagramCredentialDTO.builder()
                                .id(instagramCredentialEntity.getId())
                                .expirateTime(instagramCredentialEntity.getExpirateTime())
                                .createdTime(instagramCredentialEntity.getCreatedTime())
                                .build()
                ).orElse(null);
    }

    public Boolean hasGoogleCrendential() {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();
        if (StringUtils.hasText(companyIdUserLogged)) {
            Instant now = Instant.now();
            Optional<GoogleCredentialEntity> byUserId = googleCredentialRepository.findByUserId(companyIdUserLogged);
            return byUserId.isEmpty() || byUserId.get().getCredential().getExpirationTimeMillis() - now.toEpochMilli() < 0 ? Boolean.FALSE : Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
}