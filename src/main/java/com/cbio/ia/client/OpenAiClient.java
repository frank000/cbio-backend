package com.cbio.ia.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class OpenAiClient {

    private static final Logger log = LoggerFactory.getLogger(OpenAiClient.class);
    private OpenAiApi openAiApi;
    private final ChatModel chatModel;


    public OpenAiClient(@Value("${spring.ai.openai.api-key}") String apiKey,
                        ChatModel chatModels) {
        openAiApi = new OpenAiApi(apiKey);
        chatModel = chatModels;
    }



    public String getCompletion(String prompt) {


        Prompt prompts = new Prompt(prompt,
                OpenAiChatOptions.builder()
                        .model(OpenAiApi.ChatModel.GPT_4_O_MINI)
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