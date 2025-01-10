package com.cbio.ia.service;

import com.cbio.ia.client.OpenAiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIServiceImpl implements OpenAIService {

    private final OpenAiClient aiClient;

    @Override
    public String getHintToChat(String term) {

        String prompt = String.format(
                "De acordo com essa conversa entre A e B: %s Dê somente a sugestão de resposta para a útlima mensagem entre aspas duplas.",
                term);

        return aiClient.getCompletion(prompt);
    }

    @Override
    public String getOnlyQuestionFromRag(String term) {

        String prompt = String.format(
                "Retire somente as perguntas do questionário seguinte: %s",
                term);

        return aiClient.getCompletion(prompt);
    }



}
