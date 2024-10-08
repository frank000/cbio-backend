package com.policia.df.bot.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.policia.df.bot.app.entities.CanalEntity;
import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.app.service.enuns.EtapaPadraoEnum;
import com.policia.df.bot.app.service.mapper.CanalMapper;
import com.policia.df.bot.app.service.mapper.CycleAvoidingMappingContext;
import com.policia.df.bot.app.service.utils.TelegramUtils;
import com.policia.df.bot.core.service.*;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;
import com.policia.df.bot.core.v1.dto.EntradaMensagemDTO;
import com.policia.df.bot.core.v1.dto.GitlabEventDTO;
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

    private final CanalMapper canalMapper;

    private final ChatbotForwardService forwardService;

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
            sendMessage(createResponseBodyPorChatId(String.valueOf(update.getMessage().getChatId()), "Acesso negado. Entre em contato com o administrador."), canalEntity);
            return;
        }

        Long agora = System.currentTimeMillis();

        SessaoEntity sessao = sessaoService.validaOuCriaSessaoAtivaPorUsuario(
                update.getMessage().getFrom().getId(),
                agora);

        if (sessaoService.isSessaoValidaTempo(agora, sessao)) {

            String ultimaAcao = StringUtils.hasText(sessao.getUltimaEtapa())? sessao.getUltimaEtapa() : EtapaPadraoEnum.INIT.getValor();

            List<DecisaoResposta> resposta = respostaService.decidirResposta(update.getMessage().getText(), ultimaAcao, sessao);

            usuarioService.salvarUsuario(update);

            mensagemService.salvarMensagem(update, Long.parseLong(canalEntity.getIdCanal()), sessao.getId());

            sessaoService.atualizarSessao(sessao, resposta.get(0).getAcao());

            if(resposta != null) {
                for (DecisaoResposta e : resposta) {
                    sendMessage(createResponseBodyPorChatId(String.valueOf(update.getMessage().getChatId()), e.getTexto()), canalEntity);
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
        log.info("Resultado do envio: %s".formatted(entity.toString()));

        return entity;
    }

    @Override
    public RequestBody createResponseBodyPorChatId(String chatId, String msg) {

        String mensagem = new MensagemDto().montarEnvio(msg, chatId);

        MediaType mediaType = MediaType.parse("application/json");

        return RequestBody.create(mediaType, mensagem);

    }

    @Override
    public void enviaMenssagemParaGrupo(String token, String cliente, GitlabEventDTO obj) throws Exception {
        CanalEntity canalEntity = canalService.findCanalByTokenAndCliente(token, cliente);
        String msg = "";

        if(obj != null &&  obj.getEvent(GitlabEventDTO.Chaves.OBJECTATTRIBUTES) != null){
            msg = getMenssagemMergeRequest(obj);
        }

        //estamos usando o primeiro nome para mandar mensagem para o grupo
        //Inicialmente ao configurar um canal, não temos tal informação
        //TODO ao evoluir, fazer um passo onde possamos adicionar um novo id do grupo.
        String groupId = "-" + canalEntity.getPrimeiroNome();

        sendMessage(
                createResponseBodyPorChatId(
                        groupId,
                        msg),
                canalEntity);
    }

    private String getMenssagemMergeRequest(GitlabEventDTO obj) {
        String msg;
        String acao = "%s: %s \n"
                .formatted(
                        TelegramUtils.bold("Ação"),
                        obj.getEvent(GitlabEventDTO.Chaves.OBJECTATTRIBUTES).get(GitlabEventDTO.Chaves.ACTION));

        String detalhe = "%s: %s %s %s \n"
                .formatted(
                        TelegramUtils.bold("Branchs de"),
                        obj.getEvent(GitlabEventDTO.Chaves.OBJECTATTRIBUTES).get(GitlabEventDTO.Chaves.SOURCEBRANCH),
                        TelegramUtils.bold("--->>>"),
                        obj.getEvent(GitlabEventDTO.Chaves.OBJECTATTRIBUTES).get(GitlabEventDTO.Chaves.TARGETBRANCH)
                );

        String usuario = "%s: %s \n"
                .formatted(
                        TelegramUtils.bold("Usuário"),
                        obj.getEvent(GitlabEventDTO.Chaves.USER).get(GitlabEventDTO.Chaves.NAME));

        String projeto = "%s: %s \n".formatted(
                TelegramUtils.bold("Projeto"),
                obj.getEvent(GitlabEventDTO.Chaves.PROJETO).get(GitlabEventDTO.Chaves.NAME));

        msg =  TelegramUtils.bold("MERGE REQUEST") + "\n" +
                projeto +
                acao +
                detalhe +
                usuario;
        return msg;
    }

    @Override
    public void processaMensagem(Update update, CanalEntity canalEntity) {
        if(update.getMessage() == null && update.getCallbackQuery() == null) return;

        EntradaMensagemDTO entradaMensagemDTO = EntradaMensagemDTO
                .builder()
                .mensagem(update.getMessage() != null ? update.getMessage().getText() : update.getCallbackQuery().getData())
                .canal(canalMapper.canalEntityToCanalDTO(canalEntity, new CycleAvoidingMappingContext()))
                .identificadorRemetente(getIdentificadorRemetente(update))
                .build();
        try{
            forwardService.processaMensagem(entradaMensagemDTO);
        } catch (Exception e) {
            String msg = String.format("Exceção: %s", e.getMessage());
            throw new RuntimeException(msg);
        }


    }

    private static String getIdentificadorRemetente(Update update) {
        boolean isMsgEnviadaDiretamentePeloUsuario = update.getMessage() != null && update.getMessage().getChatId() != null;

        return String.valueOf(isMsgEnviadaDiretamentePeloUsuario ? update.getMessage().getChatId() :  update.getCallbackQuery().getMessage().getChatId());
    }
}
