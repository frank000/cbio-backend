package com.policia.df.bot.app.service.serder;

import com.policia.df.bot.app.client.TelegramClient;
import com.policia.df.bot.core.v1.dto.DialogoDTO;
import com.policia.df.bot.core.v1.dto.EntradaMensagemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component("telegramSenderService")
@RequiredArgsConstructor
public class TelegramSenderService implements Sender {

    private final TelegramClient telegramClient;

    @Override
    public void envia(DialogoDTO dialogoDTO){

        try{
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(dialogoDTO.getMensagem());
            sendMessage.setChatId(dialogoDTO.getIdentificadorRemetente());
            sendMessage.setParseMode("markdown");

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
