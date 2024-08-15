package com.policia.df.bot.core.service;

import com.policia.df.bot.app.entities.CanalEntity;
import com.policia.df.bot.core.v1.dto.GitlabEventDTO;
import okhttp3.RequestBody;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface TelegramService {

    void connectToBot(Update update, CanalEntity canal) throws Exception;

    Object sendMessage(RequestBody body, CanalEntity canal) throws IOException;

    RequestBody createResponseBodyPorChatId(String chatId, String msg);

    void enviaMenssagemParaGrupo(String token, String cliente, GitlabEventDTO obj) throws Exception;

    void processaMensagem(Update update, CanalEntity canalEntity);
}
