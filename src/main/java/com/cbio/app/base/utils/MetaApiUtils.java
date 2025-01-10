package com.cbio.app.base.utils;

import com.cbio.core.v1.dto.meta.CustomWebhookObject;
import com.restfb.DefaultJsonMapper;
import com.restfb.JsonMapper;
import com.restfb.types.webhook.WebhookObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetaApiUtils {

    public static String RECEBIDO = "EVENT_RECEIVED";

    public static String MODE_PERMITIDO = "subscribe";

    public static WebhookObject toWebhookObject(String json){
        JsonMapper mapper = new DefaultJsonMapper();
        return mapper.toJavaObject(json.toString(), CustomWebhookObject.class);
    }

//    public static String getBotToken(ConfiguracaoCanalEntity configuracaoCanalEntity) {
//        return  configuracaoCanalEntity.getCredenciais().getChave();
//    }
}
