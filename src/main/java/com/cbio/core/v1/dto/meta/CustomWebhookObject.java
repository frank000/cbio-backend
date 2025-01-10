package com.cbio.core.v1.dto.meta;

import com.restfb.types.webhook.WebhookObject;

import java.util.Optional;


public class CustomWebhookObject extends WebhookObject {
    public String getIdentificadorUsuario() {
        Optional<String> result = this.getEntryList().stream().map(entry -> {
            return entry.getMessaging().size() > 0 ? entry.getMessaging().get(0) : null;
        }).map(messagingItem -> {
            return messagingItem.getSender().getId();
        }).findAny();
        return result.get();
    }
}
