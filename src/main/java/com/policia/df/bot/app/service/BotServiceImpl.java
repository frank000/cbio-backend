package com.policia.df.bot.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.app.entities.UsuarioEntity;
import com.policia.df.bot.core.service.*;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;
import com.policia.df.bot.core.v1.dto.MensagemDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;


import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
@Slf4j
@Data
public class BotServiceImpl implements BotService {

    private final UsuarioService usuarioService;

    private final MensagemService mensagemService;

    private final SessaoService sessaoService;

    private final RespostaService respostaService;

    @Value("${telegram.url}")
    private String url;

    @Value("${telegram.api.key}")
    private String apiKey;

    @Value("${telegram.endpoint.send.message}")
    private String endpointSendMessage;

    Logger logger = Logger.getLogger(BotService.class.getName());

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void connectToBot(Object receive, Long canal) throws Exception {

        Update update = (Update) receive;

        if(update == null || update.getMessage() == null || update.getMessage().getText() == null) return;

        if(!("group".equals(update.getMessage().getChat().getType()) || "supergroup".equals(update.getMessage().getChat().getType()))) {
            sendMessage(createResponseBody(update, "Acesso negado. Entre em contato com o administrador."));
            return;
        }

        Long agora = System.currentTimeMillis();

        SessaoEntity sessao = sessaoService.validateSession(update, agora);

        if (sessaoService.sessaoValida(agora, sessao)) {

            DecisaoResposta resposta = respostaService.decidirResposta(update.getMessage().getText(), sessao.getUltimaAcao());

            usuarioService.salvarUsuario(update);

            mensagemService.salvarMensagem(update, canal, sessao.getId());

            sessaoService.atualizarSessao(sessao, resposta.getAcao());

            if(resposta != null) sendMessage(createResponseBody(update, resposta.getTexto()));
        } else {
            connectToBot(receive, canal);
        }

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

    private RequestBody createResponseBody(Update update, String msg) {

        String mensagem = new MensagemDto().montarEnvio(msg, update.getMessage().getChatId());

        MediaType mediaType = MediaType.parse("application/json");

        return RequestBody.create(mediaType, mensagem);

    }
}
