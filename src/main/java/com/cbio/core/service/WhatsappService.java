package com.cbio.core.service;

import com.cbio.app.entities.CanalEntity;
import com.cbio.core.v1.dto.GitlabEventDTO;
import okhttp3.RequestBody;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface WhatsappService extends ProcessableService{

    void connectToBot(Update update, CanalEntity canal) throws Exception;

    Object sendMessage(RequestBody body, CanalEntity canal) throws IOException;

}
