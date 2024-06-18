package com.policia.df.bot.core.service;

import com.policia.df.bot.app.entities.CanalEntity;
import com.policia.df.bot.core.v1.dto.MensagemDto;
import okhttp3.RequestBody;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface BotService {

    void connectToBot(Object receive, CanalEntity canal) throws Exception;

    Object sendMessage(RequestBody body, CanalEntity canal) throws IOException;

}
