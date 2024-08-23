package com.cbio.app.service.serder;

import com.cbio.app.client.TelegramClient;
import com.cbio.core.v1.dto.DialogoDTO;
import com.cbio.core.v1.dto.RasaMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component("telegramSenderService")
@RequiredArgsConstructor
public class TelegramSenderService implements Sender {

    private final TelegramClient telegramClient;

    @Override
    public void envia(DialogoDTO dialogoDTO){

        try{
            SendMessage sendMessage;

            if(dialogoDTO.getButtons().isEmpty()){

                sendMessage = new SendMessage();
                sendMessage.setText(dialogoDTO.getMensagem());
                sendMessage.setChatId(dialogoDTO.getIdentificadorRemetente());
                sendMessage.setParseMode("markdown");

            }else{
                List<RasaMessageDTO.Button> itens = dialogoDTO.getButtons();

                List<List<InlineKeyboardButton>> rows = itens
                        .stream()
                        .map(item -> Collections.singletonList(
                                        InlineKeyboardButton.builder()
                                                .text(item.getTitle())
                                                .callbackData(item.getPayload().concat("_telegram"))
                                                .build()
                                )
                        )
                        .collect(Collectors.toList());

                InlineKeyboardMarkup inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                        .keyboard(rows)
                        .build();

                sendMessage = new SendMessage();
                sendMessage.setText(dialogoDTO.getMensagem());
                sendMessage.setChatId(dialogoDTO.getIdentificadorRemetente());
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                sendMessage.setParseMode("markdown");
            }




            telegramClient.sendMessage(
                    dialogoDTO.getCanal().getApiKey(),
                    sendMessage
            );
        } catch (Exception e) {
            String msg = String.format("Envio para o canal Telegram com problema: %s", e.getMessage());
            throw new RuntimeException(msg);
        }

    }

}
