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
import com.cbio.core.service.*;
import com.cbio.core.v1.dto.CompanyConfigDTO;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.InstagramCredentialDTO;
import com.cbio.core.v1.dto.TicketDTO;
import com.cbio.ia.service.OpenAIService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@Service
public class CompanyServiceImpl implements CompanyService {

    public static final String INTENT_NAME_FAQ = "faq";
    private static final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);
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
    private final EmailService emailService;

    private final StoriesService storiesService;

    @Value("${app.rasa.targe-path}")
    public String BASE_TARGET_DIR;


    public CompanyDTO save(CompanyDTO companyDTO) throws CbioException {
        try {
            CompanyEntity entity = companyMapper.toEntity(companyDTO);

            entity.setStatusPayment(StatusPaymentEnum.TRIAL);
            entity.setDataAlteracaoStatus(CbioDateUtils.LocalDateTimes.now());

            CompanyEntity save = companyRepository.save(entity);


            CompanyConfigDTO configDTO = CompanyConfigDTO.builder()
                    .companyId(save.getId())
                    .emailCalendar(save.getEmail())
                    .build();

            saveConfigCompany(configDTO);

            // Chamando e esperando a conclusão
            try {
                directoryRasaService.copyRasaProjectAsync(save.getId())
                        .get(2, TimeUnit.MINUTES); // Espera explicitamente com timeout

                // Só executa Docker após confirmação do clone
                dockerService.buildDockerImageAndRunContainer(save.getId(), String.valueOf(save.getPorta()));
            } catch (TimeoutException e) {
                log.error("Timeout ao clonar repositório", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Operação interrompida", e);
            } catch (ExecutionException e) {
                log.error("Falha ao copiar projeto", e.getCause());
            }

//            directoryRasaService.copyRasaProject(save.getId());
//
//            dockerService.buildDockerImageAndRunContainer(save.getId(), String.valueOf(save.getPorta()));


            return companyMapper.toDto(save);

        } catch (IOException e) {
            throw new RuntimeException("Problema na geração da pasta default do chatbot");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void changeStatusPayment(String companyId, StatusPaymentEnum statusPayment) throws CbioException {
        CompanyEntity companyEntity = companyRepository.findById(companyId).orElseThrow(() -> new EntityNotFoundException("Company not found"));
        companyEntity.setDataAlteracaoStatus(CbioDateUtils.LocalDateTimes.now());

        companyEntity.setStatusPayment(statusPayment);

        companyRepository.save(companyEntity);
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
    public Optional<StatusPaymentEnum> getStatusPayment(String companyId) {
        Optional<CompanyEntity> byId = companyRepository.findById(companyId);
        if(byId.isPresent()){
            return Optional.of(byId.get().getStatusPayment());
        }else{
            return Optional.ofNullable(null);
        }
    }

    @Override
    public CompanyDTO edit(CompanyDTO companyDTO) {
        CompanyEntity companyEntity = companyRepository.findById(companyDTO.getId())
                .orElseThrow(() -> new RuntimeException("Companhia não encontrada."));

        if(!companyEntity.getStatusPayment().equals(companyDTO.getStatusPayment())){
            companyDTO.setDataAlteracaoStatus(CbioDateUtils.LocalDateTimes.now());
        }

        companyMapper.fromDto(companyDTO, companyEntity);

        return companyMapper.toDto(companyRepository.save(companyEntity));
    }

    public Integer getFreePort() {
        CompanyEntity company = companyRepository.findFirstByOrderByPortaDesc()
                .orElseThrow(() -> new NotFoundException("Nenhum encontrado"));

        return company.getPorta() + 1;
    }

    @Override
    public Integer getPortByIdCompany(String id) {
        return findById(id).getPorta();
    }

    public CompanyConfigDTO saveConfigCompany(CompanyConfigDTO dto) throws CbioException, IOException, InterruptedException {
        try {
            CompanyConfigEntity entity;
            boolean updateRagFields = false;
            boolean isNewConfigAndHasRag = false;
            if (StringUtils.hasText(dto.getId())) {
                entity = companyConfigRepository.findById(dto.getId())
                        .orElseThrow(() -> new CbioException("Configuração não encontrada.", HttpStatus.NO_CONTENT.value()));

                isNewConfigAndHasRag = CollectionUtils.isEmpty(entity.getRag()) &&  !CollectionUtils.isEmpty(dto.getRag());

                updateRagFields = !CollectionUtils.isEmpty(entity.getRag()) && dto.getRag() != null && !entity.getRag().get(0).equals(dto.getRag().get(0));



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


            if ( dto.getRag() != null && (updateRagFields ||
                    isNewConfigAndHasRag )) {
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean isCompanyScheduler() throws IOException {
        String companyId = authService.getCompanyIdUserLogged();

        if(companyId != null){
            return companyConfigRepository.existsByCompanyIdAndIsSchedulerTrue(companyId);
        }else{
            return Boolean.FALSE;
        }
    }

    public void toggleSchedulingToCompany() throws IOException {

        String companyId = authService.getCompanyIdUserLogged();

        if(companyId != null){
            CompanyConfigEntity companyConfigEntity = companyConfigRepository.findByCompanyId(companyId).orElseThrow();
            CompanyConfigDTO dto = companyConfigMapper.toDto(companyConfigEntity);

            if(Boolean.FALSE.equals(companyConfigEntity.getIsScheduler())){

                fullUpdateNLUFromRasaToScheduling(dto);
                companyConfigEntity.setIsScheduler(Boolean.TRUE);

            }else{
                ResultFileNlu result = getResultFileNlu(dto);
                removeSchedulingIntents(result.nluConfig());

                companyConfigEntity.setIsScheduler(Boolean.FALSE);

            }
            companyConfigRepository.save(companyConfigEntity);

        }

    }


    @Async
    protected void fullUpdateNLUFromRasaToScheduling(CompanyConfigDTO dto) {
        try {
            ResultFileNlu result = getResultFileNlu(dto);

            // Adiciona ou atualiza os intents de agendamento
            updateSchedulingIntents(result.nluConfig());

            nluYamlManagerServiceImpl.saveNluFile(result.nluFilePath(), result.nluConfig());
            runDocker(dto.getCompanyId());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateSchedulingIntents(NluYamlManagerServiceImpl.NluConfig nluConfig) {
        // Intent: agendamento
        nluYamlManagerServiceImpl.removeIntent(nluConfig, "agendamento");
        List<String> agendamentoExamples = Arrays.asList(
                "Quero agendar",
                "Desejo marcar uma agendamento",
                "Pode marcar um dia para min?",
                "Pode marcar um horário por favor?",
                "Quero agendar uma consulta para próxima semana",
                "Preciso marcar um horário com o dentista",
                "Como posso agendar um serviço?",
                "Gostaria de reservar um horário para corte de cabelo",
                "Pode me ajudar a agendar uma reunião?",
                "Quero marcar uma sessão de terapia",
                "Desejo agendar uma avaliação médica",
                "Qual horário disponível para manutenção do meu carro?",
                "Preciso de um agendamento com o veterinário",
                "Como faço para marcar um horário no salão?",
                "Quero reservar uma consulta com o médico",
                "Posso agendar um serviço de encanamento?",
                "Gostaria de marcar uma consulta com o oftalmologista",
                "Desejo agendar uma massagem",
                "Pode me ajudar a marcar um horário para meu filho?",
                "Quero reservar uma consulta com o nutricionista",
                "Como posso agendar um serviço de limpeza?",
                "Preciso marcar um horário no mecânico",
                "Gostaria de agendar uma consulta de fisioterapia",
                "Desejo marcar um horário para documentos"
        );
        nluYamlManagerServiceImpl.addIntent(nluConfig, "agendamento", agendamentoExamples);

        // Intent: agendamento_confirmacao
        nluYamlManagerServiceImpl.removeIntent(nluConfig, "agendamento_confirmacao");
        List<String> confirmacaoExamples = Arrays.asList(
                "Sim",
                "Quero agendar",
                "Claro",
                "Vamos lá"
        );
        nluYamlManagerServiceImpl.addIntent(nluConfig, "agendamento_confirmacao", confirmacaoExamples);

        // Intent: escolher_recurso
        nluYamlManagerServiceImpl.removeIntent(nluConfig, "escolher_recurso");
        List<String> recursoExamples = Arrays.asList(
                "Pode ser o [672c2781ae950201dedb39f7](recurso)",
                "Doutor [672c2781ae950201dedb39f7](recurso)",
                "recurso [672c2781ae950201dedb39f7](recurso)",
                "/escolher_recurso{\"recurso\": \"672c2781ae950201dedb39f7\"}"
        );
        nluYamlManagerServiceImpl.addIntent(nluConfig, "escolher_recurso", recursoExamples);

        // Intent: escolher_data
        nluYamlManagerServiceImpl.removeIntent(nluConfig, "escolher_data");
        List<String> dataExamples = Arrays.asList(
                "quero agendar para [04/01/2025](data)",
                "escolher data [04/01/2025](data)",
                "data [04/01/2025](data)",
                "/escolher_data{\"data\": \"04/01/2025\"}"
        );
        nluYamlManagerServiceImpl.addIntent(nluConfig, "escolher_data", dataExamples);

        // Intent: escolher_hora
        nluYamlManagerServiceImpl.removeIntent(nluConfig, "escolher_hora");
        List<String> horaExamples = Arrays.asList(
                "hora escolhida [2025-02-01T12:00&2025-02-01T13:00](horario)",
                "agendar para hora [2025-02-01T12:00&2025-02-01T13:00](horario)",
                "escolher data [2025-02-01T12:00&2025-02-01T13:00](horario)",
                "hora [2025-02-01T12:00&2025-02-01T13:00](horario)",
                "/escolher_hora{\"horario\": \"2025-01-04T12:00&2025-02-01T13:00\"}"
        );
        nluYamlManagerServiceImpl.addIntent(nluConfig, "escolher_hora", horaExamples);
    }

    @Async
    protected void fullUpdateNLUFromRasa(CompanyConfigDTO dto, String onlyQuestionFromRag) {
        try {

            ResultFileNlu result = getResultFileNlu(dto);

            nluYamlManagerServiceImpl.removeIntent(result.nluConfig(), INTENT_NAME_FAQ);
            nluYamlManagerServiceImpl.addIntent(result.nluConfig(), INTENT_NAME_FAQ, getExempleList(onlyQuestionFromRag));
            nluYamlManagerServiceImpl.saveNluFile(result.nluFilePath(), result.nluConfig());

            runDocker(dto.getCompanyId());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        geraTicket(dto);
    }

    @NotNull
    private ResultFileNlu getResultFileNlu(CompanyConfigDTO dto) throws IOException {
        String nluFilePath = BASE_TARGET_DIR
                .concat(File.separator)
                .concat(dto.getCompanyId())
                .concat(File.separator)
                .concat("data/nlu.yml");

        NluYamlManagerServiceImpl.NluConfig nluConfig = nluYamlManagerServiceImpl.readNluFile(nluFilePath);
        ResultFileNlu result = new ResultFileNlu(nluFilePath, nluConfig);
        return result;
    }

    @Async
    protected void disableSchedulingIntents(CompanyConfigDTO dto) {
        try {
            ResultFileNlu result = getResultFileNlu(dto);

            // Remove todos os intents de agendamento
            removeSchedulingIntents(result.nluConfig());

            nluYamlManagerServiceImpl.saveNluFile(result.nluFilePath(), result.nluConfig());
            runDocker(dto.getCompanyId());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private void removeSchedulingIntents(NluYamlManagerServiceImpl.NluConfig nluConfig) {
        // Lista de todos os intents de agendamento que devem ser removidos
        List<String> schedulingIntents = Arrays.asList(
                "agendamento",
                "agendamento_confirmacao",
                "escolher_recurso",
                "escolher_data",
                "escolher_hora"
        );

        // Remove cada intent da lista
        schedulingIntents.forEach(intentName ->
                nluYamlManagerServiceImpl.removeIntent(nluConfig, intentName)
        );
    }
    private record ResultFileNlu(String nluFilePath, NluYamlManagerServiceImpl.NluConfig nluConfig) {
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

        TicketDTO savedTicket = ticketService.save(ticketDTO, null);
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

    @Override
    public StatusPaymentEnum getStatusPayment() {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();

        Optional<CompanyEntity> byId = companyRepository.findById(companyIdUserLogged);
        if(byId.isPresent()){
            return byId.get().getStatusPayment();
        }else{
            return null;
        }
    }

    public Object getPaymentInfoAboutCompanyLogged() {
        String companyIdUserLogged = authService.getCompanyIdUserLogged();

        if(companyIdUserLogged != null) {
            return getPaymentInfoAboutCompany(companyIdUserLogged);
        }else{
            return null;
        }
    }

    public Object getPaymentInfoAboutCompany(String id) {
        return null;
    }

    @Override
    public void completeProfile(String id, String pass) throws MessagingException {
        Optional<CompanyEntity> companyEntity = companyRepository.findById(id).stream().findFirst();
        if(companyEntity.isPresent()) {
            CompanyEntity company = companyEntity.get();

            Map<String, Object> model = new HashMap<>();
            model.put("companyName", company.getNome());
            model.put("tier", company.getTier());
            model.put("emailAdministrador", company.getEmail());
            model.put("passwordAdministrador", pass);
            model.put("modo", "trial");

            emailService.enviarEmailModel(
                    company.getEmail(),
                    "Bem vindo a RayzaTEC - Acesso ao Dunamis",
                    "email-welcome.ftlh",
                    model
                    );
        }else{
            log.error("COMPANY SERVICE: Não pode completar o formulario, id: " + id);
        }
    }
}