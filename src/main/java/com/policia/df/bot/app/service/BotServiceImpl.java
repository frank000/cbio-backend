package com.policia.df.bot.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.policia.df.bot.app.entities.UsuarioEntity;
import com.policia.df.bot.core.service.BotService;
import com.policia.df.bot.core.service.UsuarioService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;


import java.io.IOException;
import java.util.logging.Logger;

@Service
@Slf4j
@Data
public class BotServiceImpl implements BotService {

    private final UsuarioService usuarioService;

    @Value("${telegram.url}")
    private String url;

    @Value("${telegram.api.key}")
    private String apiKey;

    @Value("${telegram.endpoint.send.message}")
    private String endpointSendMessage;

    Logger logger = Logger.getLogger(BotService.class.getName());

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object connectToBot(Update update) throws Exception {

        UsuarioEntity usuario = usuarioService.buscarUsuarioPorIdUsuario(update.getMessage().getChatId());

        if(usuario == null) {
            usuario = new UsuarioEntity();

            usuario.setIdUsuario(update.getMessage().getChatId());
            usuario.setFirtName(update.getMessage().getChat().getFirstName());
            usuario.setLastName(update.getMessage().getChat().getLastName());
        }

        try {
            usuarioService.cadastrarUsuario(usuario);
        } catch (Exception e) {
            throw new Exception();
        }


        OkHttpClient client = new OkHttpClient();

        logger.info("Passou por aqui. " + update.getMessage().getText() + " " + update.getMessage().getChatId());

        return new String("Será que retorna?");
    }

    @Override
    public Object sendMessage(RequestBody body) throws IOException {

        String endpoint = new StringBuilder(url)
                .append(apiKey)
                .append(endpointSendMessage)
                .toString();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(endpoint)
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("User-Agent", "Telegram Bot SDK - (https://github.com/irazasyed/telegram-bot-sdk)")
                .addHeader("content-type", "application/json")
                .build();

        ResponseBody response = client.newCall(request).execute().body();

        var entity = objectMapper.readValue(response.string(), Object.class);

        return entity;
    }
}
