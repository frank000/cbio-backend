package com.cbio.app.service.serder;

import com.cbio.app.client.TelegramClient;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.service.SessaoService;
import com.cbio.core.v1.dto.RasaMessageDTO;
import com.whatsapp.api.WhatsappApiFactory;
import com.whatsapp.api.configuration.ApiVersion;
import com.whatsapp.api.domain.messages.*;
import com.whatsapp.api.domain.messages.response.MessageResponse;
import com.whatsapp.api.domain.messages.type.ButtonType;
import com.whatsapp.api.domain.messages.type.InteractiveMessageType;
import com.whatsapp.api.impl.WhatsappBusinessCloudApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component("whatsappSenderService")
@RequiredArgsConstructor
public class WhatsappSenderService implements Sender {

    private final TelegramClient telegramClient;
    private final SessaoService sessaoService;

    @Override
    public void envia(DialogoDTO dialogoDTO) {

        try {
            SendMessage sendMessage;
            Message message;

            if (dialogoDTO.getButtons().isEmpty()) {

                message = Message.MessageBuilder.builder()//
                        .setTo("+" + dialogoDTO.getIdentificadorRemetente())//
                        .buildTextMessage(new TextMessage()//
                                .setBody(dialogoDTO.getMensagem())//
                                .setPreviewUrl(false));
            } else {

                List<RasaMessageDTO.Button> itens = dialogoDTO.getButtons();
                Action actionButtons = new Action();
                itens
                        .stream()
                        .forEach(
                                button -> {
                                    actionButtons.addButton(
                                            new Button()
                                                    .setType(ButtonType.REPLY)
                                                    .setReply(new Reply()
                                                            .setId(button.getPayload())
                                                            .setTitle(button.getTitle()
                                                            ))
                                    );
                                }
                        );
                message = Message.MessageBuilder.builder()//
                        .setTo("+" + dialogoDTO.getIdentificadorRemetente())//
                        .buildInteractiveMessage(
                                InteractiveMessage.build() //
                                        .setAction(actionButtons) //
                                        .setType(InteractiveMessageType.BUTTON) //
                                        .setBody(new Body() //
                                                .setText(dialogoDTO.getMensagem())) //
                        );



            }

            WhatsappApiFactory factory = WhatsappApiFactory.newInstance(dialogoDTO.getCanal().getApiKey());
            WhatsappBusinessCloudApi whatsappBusinessCloudApi = factory.newBusinessCloudApi(ApiVersion.V20_0);


            MessageResponse messageResponse = whatsappBusinessCloudApi.sendMessage(dialogoDTO.getCanal().getIdCanal(), message);
            System.out.println(messageResponse);

        } catch (Exception e) {
            String msg = String.format("Envio para o canal Telegram com problema: %s", e.getMessage());
            throw new RuntimeException(msg);
        }

    }

}
