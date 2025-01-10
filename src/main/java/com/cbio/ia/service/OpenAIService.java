package com.cbio.ia.service;

public interface OpenAIService {

    String getHintToChat(String prompt);

    String getOnlyQuestionFromRag(String prompt);
}
