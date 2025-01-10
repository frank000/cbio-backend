package com.cbio.app.meta.facebook.service.client;

import com.restfb.DefaultFacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.send.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;

import java.util.Optional;

@Component
public class FacebookClient {


    public Optional<SendResponse> sendMessageGeneric(
            String token,
            String identificadorUsuario,
            TemplatePayload payload){
        TemplateAttachment templateAttachment = new TemplateAttachment(payload);
        Message message = new Message(templateAttachment);

        com.restfb.FacebookClient pageClient = new DefaultFacebookClient(token, Version.VERSION_18_0);
        IdMessageRecipient recipient = new IdMessageRecipient(identificadorUsuario);

        SendResponse resp = pageClient.publish("me/messages",
                SendResponse.class,
                Parameter.with("recipient", recipient), // the id or phone recipient
                Parameter.with("message", message));
        return Optional.ofNullable(resp);
    }

    public Optional<SendResponse> sendImage(
            String token,
            String identificadorUsuario,
            MediaAttachment payload){
        Message message = new Message(payload);

        com.restfb.FacebookClient pageClient = new DefaultFacebookClient(token, Version.VERSION_18_0);
        IdMessageRecipient recipient = new IdMessageRecipient(identificadorUsuario);

        SendResponse resp = pageClient.publish("me/messages",
                SendResponse.class,
                Parameter.with("recipient", recipient), // the id or phone recipient
                Parameter.with("message", message));
        return Optional.ofNullable(resp);
    }

    public Optional<SendResponse> sendMessageText(
            String token,
            String identificadorUsuario,
            String payload){
        Message message = new Message(payload);

        com.restfb.FacebookClient pageClient = new DefaultFacebookClient(token, Version.VERSION_18_0);
        IdMessageRecipient recipient = new IdMessageRecipient(identificadorUsuario);

        SendResponse resp = pageClient.publish("me/messages",
                SendResponse.class,
                Parameter.with("recipient", recipient), // the id or phone recipient
                Parameter.with("message", message));
        return Optional.ofNullable(resp);
    }


    public Optional<SendResponse> sendMessageTemplate(
            String token,
            String identificadorUsuario,
            TemplateAttachment payload){
        Message message = new Message(payload);

        com.restfb.FacebookClient pageClient = new DefaultFacebookClient(token, Version.VERSION_18_0);
        IdMessageRecipient recipient = new IdMessageRecipient(identificadorUsuario);

        SendResponse resp = pageClient.publish("me/messages",
                SendResponse.class,
                Parameter.with("recipient", recipient), // the id or phone recipient
                Parameter.with("message", message));
        return Optional.ofNullable(resp);
    }

//    public Optional<ApiResponse<org.telegram.telegrambots.meta.api.objects.Message>> sendMessage(String token, BaseMessageDTO baseMessageDTO){
//        com.restfb.FacebookClient pageClient = new DefaultFacebookClient(token, Version.VERSION_18_0);
//        IdMessageRecipient recipient = new IdMessageRecipient(baseMessageDTO.getDestino().getIdentificadorUsuarioCanal());
//        Message message = new Message("Resposta nova;");
//        SendResponse resp = pageClient.publish("me/messages", SendResponse.class, Parameter.with("recipient", recipient), // the id or phone recipient
//                Parameter.with("message", message));
//        return null;
//    }
}
