package com.policia.df.bot.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.policia.df.bot.app.entities.CanalEntity;
import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.app.service.enuns.EtapaPadraoEnum;
import com.policia.df.bot.core.service.*;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;
import com.policia.df.bot.core.v1.dto.MensagemDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;


import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Service
@Slf4j
@Data
public class TelegramServiceImpl implements TelegramService {

    private final UsuarioService usuarioService;

    private final MensagemService mensagemService;

    private final SessaoService sessaoService;

    private final RespostaService respostaService;

    private final CanalService canalService;

    @Value("${telegram.url}")
    private String url;

    @Value("${telegram.endpoint.send.message}")
    private String endpointSendMessage;

    Logger logger = Logger.getLogger(TelegramService.class.getName());

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void connectToBot(Update update, CanalEntity canalEntity) throws Exception {

        if(update.getMessage() == null && update.getCallbackQuery() == null) return;

        boolean isGrupo = "group".equals(update.getMessage().getChat().getType());
        boolean isSuperGrupo = "supergroup".equals(update.getMessage().getChat().getType());

        if(!(isGrupo || isSuperGrupo)) {
            sendMessage(createResponseBody(update, "Acesso negado. Entre em contato com o administrador."), canalEntity);
            return;
        }

        Long agora = System.currentTimeMillis();

        SessaoEntity sessao = sessaoService.validateSession(update, agora);

        if (sessaoService.sessaoValida(agora, sessao)) {

            String ultimaAcao = StringUtils.hasText(sessao.getUltimaEtapa())? sessao.getUltimaEtapa() : EtapaPadraoEnum.INIT.getValor();

            List<DecisaoResposta> resposta = respostaService.decidirResposta(update.getMessage().getText(), ultimaAcao, sessao);

            usuarioService.salvarUsuario(update);

            mensagemService.salvarMensagem(update, Long.parseLong(canalEntity.getIdCanal()), sessao.getId());

            sessaoService.atualizarSessao(sessao, resposta.get(0).getAcao());

            if(resposta != null) {
                for (DecisaoResposta e : resposta) {
                    sendMessage(createResponseBody(update, e.getTexto()), canalEntity);
                }
            }
        } else {
            connectToBot(update, canalEntity);
        }

    }

    @Override
    public Object sendMessage(RequestBody body, CanalEntity canal) throws IOException {

        String endpoint = new StringBuilder(url)
                .append(canal.getApiKey())
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
