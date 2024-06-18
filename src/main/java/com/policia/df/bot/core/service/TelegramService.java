package com.policia.df.bot.core.service;

import com.policia.df.bot.app.entities.CanalEntity;
import okhttp3.RequestBody;

import java.io.IOException;

public interface TelegramService {

    void connectToBot(Object receive, CanalEntity canal) throws Exception;

    Object sendMessage(RequestBody body, CanalEntity canal) throws IOException;

}
