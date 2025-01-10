package com.cbio.ia.client;


import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


@Service
public class OpenAiClient {

    private OpenAiApi openAiApi;
    private final ChatModel chatModel;

    public OpenAiClient(@Value("${spring.ai.openai.api-key}") String apiKey,
                        ChatModel chatModels) {
        openAiApi = new OpenAiApi(apiKey);
        chatModel = chatModels;
    }


    public String getCompletion(String prompt) {

        ChatResponse response = chatModel.call(
                new Prompt(
                        prompt,
                        OpenAiChatOptions.builder()
                                .withModel(OpenAiApi.ChatModel.GPT_3_5_TURBO.getName())
                                .withTemperature(0.4)
                                .build()
                ));
        return response
                .getResults()
                .stream()
                .map(generation -> generation.getOutput().getContent())
                .collect(Collectors.joining("\n"));
    }
}