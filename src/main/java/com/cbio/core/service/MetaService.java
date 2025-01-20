package com.cbio.core.service;

import com.restfb.types.webhook.WebhookObject;

public interface MetaService {

    void exchangeCodeToTokenAndSave(String state, String code) throws Exception;

    void processaMensagem(WebhookObject webhookObject) throws Exception;
}
