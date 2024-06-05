package com.policia.df.bot.core.service;

import com.policia.df.bot.app.entities.SessaoEntity;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface SessaoService {

    String createSession(Update update);

}
