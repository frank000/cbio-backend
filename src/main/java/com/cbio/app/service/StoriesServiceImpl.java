package com.cbio.app.service;

import com.cbio.core.service.StoriesService;
import com.cbio.core.v1.dto.CompanyConfigDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class StoriesServiceImpl implements StoriesService {


    private final StoriesYamlManagerServiceImpl storiesYamlManagerServiceImpl;

    @Value("${app.rasa.targe-path}")
    public String base;

    public StoriesServiceImpl(StoriesYamlManagerServiceImpl storiesYamlManagerServiceImpl) {
        this.storiesYamlManagerServiceImpl = storiesYamlManagerServiceImpl;
    }


    public void addSchedulingStories(CompanyConfigDTO dto) {
        try {
            ResultFileStories result = getResultFileStories(dto);

            // Adiciona todas as stories de agendamento
            addAllSchedulingStories(result.storiesConfig());

            storiesYamlManagerServiceImpl.saveStoriesFile(result.storiesFilePath(), result.storiesConfig());
            runDocker(dto.getCompanyId());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void removeSchedulingStories(CompanyConfigDTO dto) {
        try {
            ResultFileStories result = getResultFileStories(dto);

            // Remove todas as stories de agendamento
            removeAllSchedulingStories(result.storiesConfig());

            storiesYamlManagerServiceImpl.saveStoriesFile(result.storiesFilePath(), result.storiesConfig());
            runDocker(dto.getCompanyId());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private ResultFileStories getResultFileStories(CompanyConfigDTO dto) throws IOException {

        String storiesFilePath = base
                .concat(File.separator)
                .concat(dto.getCompanyId())
                .concat(File.separator)
                .concat("data/stories.yml");

        StoriesYamlManagerServiceImpl.StoriesConfig storiesConfig = storiesYamlManagerServiceImpl.readStoriesFile(storiesFilePath);
        return new ResultFileStories(storiesFilePath, storiesConfig);
    }
    private void addAllSchedulingStories(StoriesYamlManagerServiceImpl.StoriesConfig storiesConfig) {
        // Story 1: Perguntar se deseja agendar um atendimento
        storiesYamlManagerServiceImpl.addStory(
                storiesConfig,
                "Perguntar se deseja agendar um atendimento",
                Arrays.asList(
                        storiesYamlManagerServiceImpl.createIntentStep("agendamento", null),
                        storiesYamlManagerServiceImpl.createActionStep("utter_inicio_atendimento")
                )
        );

        // Story 2: Inicia o agendamento de atendimento
        storiesYamlManagerServiceImpl.addStory(
                storiesConfig,
                "Inicia o agendamento de atendimento",
                Arrays.asList(
                        storiesYamlManagerServiceImpl.createIntentStep("agendamento_confirmacao", null),
                        storiesYamlManagerServiceImpl.createActionStep("acao_inicia_agendamento")
                )
        );

        // Story 3: Seleciona o recurso do agendamento
        storiesYamlManagerServiceImpl.addStory(
                storiesConfig,
                "Seleciona o recurso do agendamento",
                Arrays.asList(
                        storiesYamlManagerServiceImpl.createIntentStep("escolher_recurso",
                                Map.of("recurso", "672c2781ae950201dedb39f7")),
                        storiesYamlManagerServiceImpl.createActionStep("acao_recurso_escolhida")
                )
        );

        // Story 4: Seleciona data do agendamento
        storiesYamlManagerServiceImpl.addStory(
                storiesConfig,
                "Seleciona data do agendamento de atendimento",
                Arrays.asList(
                        storiesYamlManagerServiceImpl.createIntentStep("escolher_data",
                                Map.of("data", "04/01/2025")),
                        storiesYamlManagerServiceImpl.createActionStep("acao_data_escolhida")
                )
        );

        // Story 5: Seleciona horário do agendamento
        storiesYamlManagerServiceImpl.addStory(
                storiesConfig,
                "Seleciona horario agendamento",
                Arrays.asList(
                        storiesYamlManagerServiceImpl.createIntentStep("escolher_hora",
                                Map.of("horario", "2025-02-01T12:00&2025-02-01T13:00")),
                        storiesYamlManagerServiceImpl.createActionStep("acao_horario_escolhida")
                )
        );

        // Story 6: Capturar nome quando informado
        storiesYamlManagerServiceImpl.addStory(
                storiesConfig,
                "Capturar nome quando informado",
                Arrays.asList(
                        storiesYamlManagerServiceImpl.createIntentStep("informar_nome",
                                Map.of("nome", "")),
                        storiesYamlManagerServiceImpl.createActionStep("action_receber_nome")
                )
        );

        // Story 7: Capturar o contato informado
        storiesYamlManagerServiceImpl.addStory(
                storiesConfig,
                "Capturar o contato informado",
                Arrays.asList(
                        storiesYamlManagerServiceImpl.createIntentStep("informar_contato",
                                Map.of("contato", "")),
                        storiesYamlManagerServiceImpl.createActionStep("action_receber_contato")
                )
        );
    }


    private void removeAllSchedulingStories(StoriesYamlManagerServiceImpl.StoriesConfig storiesConfig) {
        List<String> schedulingStories = Arrays.asList(
                "Perguntar se deseja agendar um atendimento",
                "Inicia o agendamento de atendimento",
                "Seleciona o recurso do agendamento",
                "Seleciona data do agendamento de atendimento",
                "Seleciona horario agendamento",
                "Capturar nome quando informado",
                "Capturar o contato informado"
        );

        schedulingStories.forEach(storyName ->
                storiesYamlManagerServiceImpl.removeStory(storiesConfig, storyName)
        );
    }

    private void runDocker(String companyId) throws IOException, InterruptedException {
        // Implementação similar à do NluService
        // ...
    }

    // Record para armazenar o resultado do arquivo de stories
    private record ResultFileStories(String storiesFilePath, StoriesYamlManagerServiceImpl.StoriesConfig storiesConfig) {}

    // Classe interna para representar os passos da story
    @Data
    @AllArgsConstructor
    private static class StoryStep {
        private String type; // "intent" ou "action"
        private String value;
        private Map<String, String> entities;
    }

}
