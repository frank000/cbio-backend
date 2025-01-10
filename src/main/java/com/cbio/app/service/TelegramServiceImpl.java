package com.cbio.app.service;

import com.cbio.app.entities.CanalEntity;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.service.enuns.EtapaPadraoEnum;
import com.cbio.app.service.mapper.CanalMapper;
import com.cbio.app.service.mapper.CycleAvoidingMappingContext;
import com.cbio.app.service.utils.TelegramUtils;
import com.cbio.core.service.*;
import com.cbio.core.v1.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Service
@Slf4j
@Data
public class TelegramServiceImpl implements TelegramService {


    private static final String URL_BOT = "https://pleasing-elf-instantly.ngrok-free.app/v1/bot/webhook";

    private static final String URL_API_CONNECT = "https://api.telegram.org/bot%s/setWebhook";

    private final UsuarioTelegramService usuarioService;

    private final MensagemService mensagemService;

    private final SessaoService sessaoService;

    private final RespostaService respostaService;

    private final CanalService canalService;

    private final CanalMapper canalMapper;

    private final RestTemplate restTemplate;

    private final ChatbotForwardService forwardService;

    @Value("${telegram.url}")
    private String url;

    @Value("${telegram.endpoint.send.message}")
    private String endpointSendMessage;

    Logger logger = Logger.getLogger(TelegramService.class.getName());

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void connectToBot(Update update, CanalEntity canalEntity) throws Exception {

        if (update.getMessage() == null && update.getCallbackQuery() == null) return;

        boolean isGrupo = "group".equals(update.getMessage().getChat().getType());
        boolean isSuperGrupo = "supergroup".equals(update.getMessage().getChat().getType());

        if (!(isGrupo || isSuperGrupo)) {
            sendMessage(createResponseBodyPorChatId(String.valueOf(update.getMessage().getChatId()), "Acesso negado. Entre em contato com o administrador."), canalEntity);
            return;
        }

        Long agora = System.currentTimeMillis();

        SessaoEntity sessao = sessaoService.validaOuCriaSessaoAtivaPorUsuarioCanal(
                update.getMessage().getFrom().getId(),
                canalMapper.canalEntityToCanalDTO(canalEntity, new CycleAvoidingMappingContext()),
                agora);

        if (sessaoService.isSessaoValidaTempo(agora, sessao)) {

            String ultimaAcao = StringUtils.hasText(sessao.getUltimaEtapa()) ? sessao.getUltimaEtapa() : EtapaPadraoEnum.INIT.getValor();

            List<DecisaoResposta> resposta = respostaService.decidirResposta(update.getMessage().getText(), ultimaAcao, sessao);

            usuarioService.salvarUsuario(update);

            mensagemService.salvarMensagem(update, Long.parseLong(canalEntity.getIdCanal()), sessao.getId());

            sessaoService.atualizarSessao(sessao, resposta.get(0).getAcao());

            if (resposta != null) {
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
        CanalEntity canalEntity = canalService.findCanalByTokenAndCliente(token, cliente)
                .orElseThrow(() -> new RuntimeException("Canal não encontrado"));
        String msg = "";

        if (obj != null && obj.getEvent(GitlabEventDTO.Chaves.OBJECTATTRIBUTES) != null) {
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

        msg = TelegramUtils.bold("MERGE REQUEST") + "\n" +
                projeto +
                acao +
                detalhe +
                usuario;
        return msg;
    }

    @Override
    public void processaMensagem(EntradaMensagemDTO entradaMensagemDTO, CanalEntity canalEntity) {

        try {
            entradaMensagemDTO.setIdentificadorRemetente(getIdentificadorRemetente((Update) entradaMensagemDTO.getMensagemObject()));
            forwardService.processaMensagem(entradaMensagemDTO);
        } catch (Exception e) {
            String msg = String.format("Exceção: %s", e.getMessage());
            throw new RuntimeException(msg);
        }


    }

    private static String getIdentificadorRemetente(Update update) {
        boolean isMsgEnviadaDiretamentePeloUsuario = update.getMessage() != null && update.getMessage().getChatId() != null;

        return String.valueOf(isMsgEnviadaDiretamentePeloUsuario ? update.getMessage().getChatId() : update.getCallbackQuery().getMessage().getChatId());
    }


    @Async
    public void connect(String canalId) {

        CanalDTO canalDTO = canalService.obtemPorId(canalId);
        String scriptPath = "./webhook.sh %s %s %s";

        String command = String.format(scriptPath, canalDTO.getApiKey(), canalDTO.getToken(), canalDTO.getCliente());

        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);

        try {
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Script executado com sucesso.");
            } else {
                System.out.println("Erro ao executar o script. Código de saída: " + exitCode);
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }


}
