package com.cbio.app.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StoriesYamlManagerServiceImpl {

    @Data
    public static class StoriesConfig {
        private String version;
        private List<Map<String, Object>> stories;

        public StoriesConfig() {
            this.stories = new ArrayList<>();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Story {
        private String story;
        private List<Step> steps;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Step {
        private String intent;
        private String action;
        private Map<String, String> entities;
    }

    public StoriesConfig readStoriesFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + filePath);
        }

        String content = Files.readString(path);
        LoaderOptions loadingConfig = new LoaderOptions();
        Yaml yaml = new Yaml(loadingConfig); // Remove o Constructor

        // Carrega como Map
        Map<String, Object> rawConfig = yaml.load(content);
        if (rawConfig == null) {
            return new StoriesConfig();
        }

        StoriesConfig config = new StoriesConfig();
        config.setVersion((String) rawConfig.get("version"));

        // Processa as stories
        List<Map<String, Object>> rawStories = (List<Map<String, Object>>) rawConfig.get("stories");
        if (rawStories != null) {
            for (Map<String, Object> rawStory : rawStories) {
                Map<String, Object> processedStory = new LinkedHashMap<>();
                processedStory.put("story", rawStory.get("story"));

                // Processa os steps
                List<Map<String, Object>> rawSteps = (List<Map<String, Object>>) rawStory.get("steps");
                List<Map<String, Object>> processedSteps = new ArrayList<>();

                if (rawSteps != null) {
                    for (Map<String, Object> rawStep : rawSteps) {
                        Map<String, Object> processedStep = new LinkedHashMap<>();

                        if (rawStep.containsKey("intent")) {
                            processedStep.put("intent", rawStep.get("intent"));

                            // Processa entities se existirem
                            if (rawStep.containsKey("entities")) {
                                List<Map<String, String>> entitiesList = (List<Map<String, String>>) rawStep.get("entities");
                                Map<String, String> entities = new LinkedHashMap<>();
                                if (entitiesList != null) {
                                    for (Map<String, String> entityMap : entitiesList) {
                                        entities.putAll(entityMap);
                                    }
                                }
                                processedStep.put("entities", entities);
                            }
                        } else if (rawStep.containsKey("action")) {
                            processedStep.put("action", rawStep.get("action"));
                        }

                        processedSteps.add(processedStep);
                    }
                }

                processedStory.put("steps", processedSteps);
                config.getStories().add(processedStory);
            }
        }

        return config;
    }


    public void saveStoriesFile(String filePath, StoriesConfig config) throws IOException {
        Objects.requireNonNull(config, "A configuração de Stories não pode ser nula");

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        options.setIndent(2);
        options.setSplitLines(false);
        options.setLineBreak(DumperOptions.LineBreak.UNIX);
        options.setWidth(1000);

        Representer representer = new Representer(options) {
            {
                this.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            }

            @Override
            protected Tag getTag(Class<?> clazz, Tag defaultTag) {
                return Tag.MAP;
            }
        };

        representer.getPropertyUtils().setSkipMissingProperties(true);

        String yamlContent = "version: \"" + config.getVersion() + "\"\n\n" +
                "stories:\n" +
                formatStories(config.getStories());

        Files.writeString(Path.of(filePath), yamlContent);
    }

    private String formatStories(List<Map<String, Object>> stories) {
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> story : stories) {
            sb.append("- story: ").append(story.get("story")).append("\n");
            sb.append("  steps:\n");

            List<Map<String, Object>> steps = (List<Map<String, Object>>) story.get("steps");
            if (steps != null) {
                for (Map<String, Object> step : steps) {
                    if (step.containsKey("intent")) {
                        sb.append("    - intent: ").append(step.get("intent")).append("\n");

                        if (step.containsKey("entities")) {
                            sb.append("      entities:\n");
                            Object entitiesObj = step.get("entities");

                            if (entitiesObj instanceof List) {
                                List<?> entitiesList = (List<?>) entitiesObj;

                                for (Object entity : entitiesList) {
                                    if (entity instanceof String) {
                                        // Caso List<String> - formato correto do Rasa
                                        sb.append("        - ").append(entity).append("\n");
                                    } else if (entity instanceof Map) {
                                        // Caso List<Map<String,String>> - formato incorreto, mas vamos tratar
                                        Map<String, String> entityMap = (Map<String, String>) entity;
                                        entityMap.forEach((key, value) -> {
                                            if (value == null || value.isEmpty()) {
                                                // Se o valor estiver vazio, só coloca a chave (nome da entity)
                                                sb.append("        - ").append(key).append("\n");
                                            } else {
                                                // Se tiver valor, mantém o formato chave: valor
                                                sb.append("        - ").append(key).append(": \"").append(value).append("\"\n");
                                            }
                                        });
                                    }
                                }
                            } else if (entitiesObj instanceof Map) {
                                // Caso Map<String,String>
                                Map<String, String> entitiesMap = (Map<String, String>) entitiesObj;
                                entitiesMap.forEach((key, value) -> {
                                    if (value == null || value.isEmpty()) {
                                        // Se o valor estiver vazio, só coloca a chave (nome da entity)
                                        sb.append("        - ").append(key).append("\n");
                                    } else {
                                        // Se tiver valor, mantém o formato chave: valor
                                        sb.append("        - ").append(key).append(": \"").append(value).append("\"\n");
                                    }
                                });
                            }
                        }
                    } else if (step.containsKey("action")) {
                        sb.append("    - action: ").append(step.get("action")).append("\n");
                    }
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void addStory(StoriesConfig config, String storyName, List<Map<String, Object>> steps) {
        Objects.requireNonNull(config, "A configuração de Stories não pode ser nula");
        Objects.requireNonNull(storyName, "O nome da story não pode ser nulo");
        Objects.requireNonNull(steps, "A lista de steps não pode ser nula");

        if (storyExists(config, storyName)) {
            throw new IllegalArgumentException("Story já existe: " + storyName);
        }

        Map<String, Object> newStory = new LinkedHashMap<>();
        newStory.put("story", storyName);
        newStory.put("steps", new ArrayList<>(steps));

        config.getStories().add(newStory);
    }

    public boolean removeStory(StoriesConfig config, String storyName) {
        Objects.requireNonNull(config, "A configuração de Stories não pode ser nula");
        Objects.requireNonNull(storyName, "O nome da story não pode ser nulo");

        return config.getStories().removeIf(story ->
                storyName.equals(((Map<String, Object>) story).get("story"))
        );
    }

    public boolean updateStorySteps(StoriesConfig config, String storyName, List<Map<String, Object>> newSteps) {
        Optional<Map<String, Object>> story = findStory(config, storyName);
        if (story.isPresent()) {
            story.get().put("steps", new ArrayList<>(newSteps));
            return true;
        }
        return false;
    }

    public boolean addStepsToStory(StoriesConfig config, String storyName, List<Map<String, Object>> additionalSteps) {
        Optional<Map<String, Object>> story = findStory(config, storyName);
        if (story.isPresent()) {
            List<Map<String, Object>> currentSteps = (List<Map<String, Object>>) story.get().get("steps");
            currentSteps.addAll(additionalSteps);
            return true;
        }
        return false;
    }

    public List<Map<String, Object>> getStorySteps(StoriesConfig config, String storyName) {
        return findStory(config, storyName)
                .map(storyMap -> (List<Map<String, Object>>) storyMap.get("steps"))
                .orElse(Collections.emptyList());
    }


    private boolean storyExists(StoriesConfig config, String storyName) {
        return findStory(config, storyName).isPresent();
    }

    private Optional<Map<String, Object>> findStory(StoriesConfig config, String storyName) {
        return config.getStories().stream()
                .filter(story -> storyName.equals(story.get("story")))
                .findFirst();
    }

    public Map<String, Object> createIntentStep(String intent, Map<String, String> entities) {
        Map<String, Object> step = new LinkedHashMap<>();
        step.put("intent", intent);
        if (entities != null && !entities.isEmpty()) {
            step.put("entities", entities.entrySet().stream()
                    .map(e -> Map.of(e.getKey(), e.getValue()))
                    .collect(Collectors.toList()));
        }
        return step;
    }

    public Map<String, Object> createActionStep(String action) {
        return Map.of("action", action);
    }


    public Map<String, Object> convertStepToMap(Step step) {
        Map<String, Object> stepMap = new LinkedHashMap<>();
        if (step.getIntent() != null) {
            stepMap.put("intent", step.getIntent());
            if (step.getEntities() != null) {
                stepMap.put("entities", step.getEntities());
            }
        } else if (step.getAction() != null) {
            stepMap.put("action", step.getAction());
        }
        return stepMap;
    }

    public Step convertMapToStep(Map<String, Object> stepMap) {
        Step step = new Step();
        if (stepMap.containsKey("intent")) {
            step.setIntent((String) stepMap.get("intent"));
            if (stepMap.containsKey("entities")) {
                step.setEntities((Map<String, String>) stepMap.get("entities"));
            }
        } else if (stepMap.containsKey("action")) {
            step.setAction((String) stepMap.get("action"));
        }
        return step;
    }
}