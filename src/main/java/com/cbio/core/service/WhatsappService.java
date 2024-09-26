package com.cbio.core.service;

import com.cbio.app.entities.CanalEntity;
import com.whatsapp.api.domain.media.MediaFile;
import com.whatsapp.api.domain.webhook.WebHookEvent;
import okhttp3.RequestBody;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface WhatsappService extends ProcessableService{

    void connectToBot(Update update, CanalEntity canal) throws Exception;

    Object sendMessage(RequestBody body, CanalEntity canal) throws IOException;

    void markMessageAsRead(String waid, CanalEntity canalEntity);

    void processEvent(String token, WebHookEvent event) throws Exception;

    MediaFile getMediaById(String id, String token);
}
