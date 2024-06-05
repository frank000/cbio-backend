package com.policia.df.bot.core.service;

import com.policia.df.bot.core.v1.dto.MensagemDto;
import okhttp3.RequestBody;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface BotService {

    public Object connectToBot(Object receive, Long canal) throws Exception;

    public Object sendMessage(RequestBody body) throws IOException;

}
