package com.cbio.app.service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import java.util.*;


public class ChatbotYamlGeneratorServiceImpl {
    public void createDomain() {
        Map<String, Object> root = new LinkedHashMap<>();

        // Criando a estrutura do chatbot
        Map<String, Object> chatbot = new LinkedHashMap<>();
        Map<String, Object> configuration = new LinkedHashMap<>();
        Map<String, Object> details = new LinkedHashMap<>();

        // Configurando details
        details.put("name", "AssistenteVirtual");
        details.put("purpose", "Atendimento ao Cliente");
        details.put("owner", Arrays.asList(
                Map.of("name", "Equipe Suporte", "email", "suporte@empresa.com"),
                Map.of("name", "Equipe Desenvolvimento", "email", "dev@empresa.com")
        ));

        // Configurando intents
        Map<String, Object> intents = new LinkedHashMap<>();

        // Intent cumprimentar
        Map<String, Object> cumprimentar = new LinkedHashMap<>();
        cumprimentar.put("examples", Arrays.asList("olá", "oi", "bom dia"));
        intents.put("cumprimentar", cumprimentar);

        // Intent despedir
        Map<String, Object> despedir = new LinkedHashMap<>();
        despedir.put("examples", Arrays.asList("tchau", "até logo", "adeus"));
        intents.put("despedir", despedir);

        // Intent solicitar_informacao
        Map<String, Object> solicitarInfo = new LinkedHashMap<>();
        solicitarInfo.put("examples", Arrays.asList(
                "preciso de ajuda com {produto}",
                "como funciona {produto}"
        ));
        solicitarInfo.put("entities", Arrays.asList("produto"));
        intents.put("solicitar_informacao", solicitarInfo);

        // Configurando entities
        Map<String, Object> entities = new LinkedHashMap<>();
        Map<String, Object> produto = new LinkedHashMap<>();
        produto.put("values", Arrays.asList("software", "aplicativo", "sistema"));
        entities.put("produto", produto);

        // Configurando slots
        Map<String, Object> slots = new LinkedHashMap<>();
        Map<String, Object> produtoSlot = new LinkedHashMap<>();
        produtoSlot.put("type", "text");
        Map<String, Object> mapping = new LinkedHashMap<>();
        mapping.put("type", "from_entity");
        mapping.put("entity", "produto");
        produtoSlot.put("mappings", Arrays.asList(mapping));
        slots.put("produto", produtoSlot);

        // Configurando responses
        Map<String, Object> responses = new LinkedHashMap<>();

        // Response utter_cumprimentar
        Map<String, Object> utterCumprimentar = new LinkedHashMap<>();
        Map<String, Object> cumprimentarMessage = new LinkedHashMap<>();
        cumprimentarMessage.put("text", "Olá! Como posso ajudar?");

        List<Map<String, String>> buttons = Arrays.asList(
                Map.of("title", "Suporte Técnico",
                        "payload", "/solicitar_informacao{\"produto\":\"software\"}"),
                Map.of("title", "Informações Gerais",
                        "payload", "/solicitar_informacao")
        );
        cumprimentarMessage.put("buttons", buttons);
        utterCumprimentar.put("messages", Arrays.asList(cumprimentarMessage));
        responses.put("utter_cumprimentar", utterCumprimentar);

        // Response utter_despedir
        Map<String, Object> utterDespedir = new LinkedHashMap<>();
        Map<String, Object> despedirMessage = new LinkedHashMap<>();
        despedirMessage.put("text", "Até logo! Tenha um ótimo dia!");
        despedirMessage.put("metadata", Map.of("sentiment", "positive"));
        utterDespedir.put("messages", Arrays.asList(despedirMessage));
        responses.put("utter_despedir", utterDespedir);

        // Montando a estrutura final
        configuration.put("details", details);
        configuration.put("intents", intents);
        configuration.put("entities", entities);
        configuration.put("slots", slots);
        configuration.put("responses", responses);

        chatbot.put("configuration", configuration);

        root.put("version", "3.1");
        root.put("chatbot", chatbot);

        // Configurando o YAML
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);

        // Gerando o output
        System.out.println(yaml.dump(root));
    }
}