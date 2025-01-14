package com.cbio.ia.client;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class OpenAiClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiClient.class);
    private OpenAiApi openAiApi;
    private final ChatModel chatModel;
    String jsonSchema = """
            {
                "type": "object",
                "properties": {
                    "steps": {
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "explanation": { "type": "string" },
                                "output": { "type": "string" }
                            },
                            "required": ["explanation", "output"],
                            "additionalProperties": false
                        }
                    },
                    "final_answer": { "type": "string" }
                },
                "required": ["steps", "final_answer"],
                "additionalProperties": false
            }
            """;

    public OpenAiClient(@Value("${spring.ai.openai.api-key}") String apiKey,
                        ChatModel chatModels) {
        openAiApi = new OpenAiApi(apiKey);
        chatModel = chatModels;
    }

    record MathReasoning(
            @JsonProperty(required = true, value = "steps") Steps steps,
            @JsonProperty(required = true, value = "final_answer") String finalAnswer) {

        record Steps(
                @JsonProperty(required = true, value = "items") Items[] items) {

            record Items(
                    @JsonProperty(required = true, value = "explanation") String explanation,
                    @JsonProperty(required = true, value = "output") String output) {
            }
        }
    }

    public String getCompletion(String prompt) {

        var outputConverter = new BeanOutputConverter<>(MathReasoning.class);

        var jsonSchema = outputConverter.getJsonSchema();
        Prompt prompts = new Prompt(prompt,
                OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.GPT_3_5_TURBO)
                        .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, jsonSchema))
                        .build());
        ChatResponse response = chatModel.call(
                prompts);


        List<Generation> results = response
                .getResults();

        return results
                .stream()
                .map(generation -> generation.getOutput().getContent())
                .collect(Collectors.joining("\n"));
    }
}