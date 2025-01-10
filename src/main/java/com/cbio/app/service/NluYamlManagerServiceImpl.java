package com.cbio.app.service;

import lombok.*;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class NluYamlManagerServiceImpl {

    @Data
    public static class NluConfig {
        private String version;
        private List<Intent> nlu;

        public NluConfig() {
            this.nlu = new ArrayList<>();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Intent {
        private String intent;
        private String examples;
    }

    public NluConfig readNluFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + filePath);
        }

        String content = Files.readString(path);
        LoaderOptions loadingConfig = new LoaderOptions();
        Yaml yaml = new Yaml(new Constructor(NluConfig.class, loadingConfig));

        NluConfig config = yaml.load(content);
        if (config == null) {
            config = new NluConfig();
        }
        return config;
    }

    public void saveNluFile(String filePath, NluConfig config) throws IOException {
        Objects.requireNonNull(config, "A configuração NLU não pode ser nula");

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

        // Configurar a ordem dos campos no YAML
        representer.getPropertyUtils().setSkipMissingProperties(true);

        String yamlContent = "version: \"" + config.getVersion() + "\"\n\n" +
                "nlu:\n" +
                formatIntents(config.getNlu());

        Files.writeString(Path.of(filePath), yamlContent);
    }

    private String formatIntents(List<Intent> intents) {
        StringBuilder sb = new StringBuilder();
        for (Intent intent : intents) {
            sb.append("- intent: ").append(intent.getIntent()).append("\n");
            sb.append("  examples: |\n");
            for (String example : parseExamples(intent.getExamples())) {
                sb.append("    - ").append(example).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void addIntent(NluConfig config, String intentName, List<String> examples) {
        Objects.requireNonNull(config, "A configuração NLU não pode ser nula");
        Objects.requireNonNull(intentName, "O nome do intent não pode ser nulo");
        Objects.requireNonNull(examples, "A lista de exemplos não pode ser nula");

        if (intentExists(config, intentName)) {
            throw new IllegalArgumentException("Intent já existe: " + intentName);
        }

        Intent newIntent = Intent.builder()
                .intent(intentName)
                .examples(formatExamples(examples))
                .build();

        config.getNlu().add(newIntent);
    }

    public boolean removeIntent(NluConfig config, String intentName) {
        Objects.requireNonNull(config, "A configuração NLU não pode ser nula");
        Objects.requireNonNull(intentName, "O nome do intent não pode ser nulo");

        return config.getNlu().removeIf(intent -> intent.getIntent().equals(intentName));
    }

    public boolean updateIntentExamples(NluConfig config, String intentName, List<String> newExamples) {
        Optional<Intent> intent = findIntent(config, intentName);
        if (intent.isPresent()) {
            intent.get().setExamples(formatExamples(newExamples));
            return true;
        }
        return false;
    }

    public boolean addExamplesToIntent(NluConfig config, String intentName, List<String> additionalExamples) {
        Optional<Intent> intent = findIntent(config, intentName);
        if (intent.isPresent()) {
            List<String> currentExamples = parseExamples(intent.get().getExamples());
            currentExamples.addAll(additionalExamples);
            intent.get().setExamples(formatExamples(currentExamples));
            return true;
        }
        return false;
    }

    public List<String> getIntentExamples(NluConfig config, String intentName) {
        return findIntent(config, intentName)
                .map(intent -> parseExamples(intent.getExamples()))
                .orElse(Collections.emptyList());
    }

    private String formatExamples(List<String> examples) {
        StringBuilder sb = new StringBuilder();
        for (String example : examples) {
            sb.append("    - ").append(example).append("\n");
        }
        return sb.toString();
    }

    private List<String> parseExamples(String examples) {
        List<String> result = new ArrayList<>();
        if (examples != null) {
            for (String line : examples.split("\n")) {
                line = line.trim();
                if (line.startsWith("- ")) {
                    result.add(line.substring(2).trim());
                }
            }
        }
        return result;
    }

    private boolean intentExists(NluConfig config, String intentName) {
        return findIntent(config, intentName).isPresent();
    }

    private Optional<Intent> findIntent(NluConfig config, String intentName) {
        return config.getNlu().stream()
                .filter(intent -> intent.getIntent().equals(intentName))
                .findFirst();
    }
}