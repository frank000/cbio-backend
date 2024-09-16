package com.cbio.app.service;

import com.cbio.app.entities.CanalEntity;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.service.enuns.EtapaPadraoEnum;
import com.cbio.app.service.mapper.CanalMapper;
import com.cbio.app.service.mapper.CycleAvoidingMappingContext;
import com.cbio.app.service.utils.TelegramUtils;
import com.cbio.core.service.*;
import com.cbio.core.v1.dto.DecisaoResposta;
import com.cbio.core.v1.dto.EntradaMensagemDTO;
import com.cbio.core.v1.dto.GitlabEventDTO;
import com.cbio.core.v1.dto.MensagemDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class WhatsappServiceImpl implements WhatsappService {

    private final UsuarioTelegramService usuarioService;

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
    public void connectToBot(Update update, CanalEntity canal) throws Exception {

    }

    @Override
    public Object sendMessage(RequestBody body, CanalEntity canal) throws IOException {
        return null;
    }

    @Override
    public void processaMensagem(EntradaMensagemDTO entradaMensagemDTO, CanalEntity canalEntity) {
        try {
            forwardService.processaMensagem(entradaMensagemDTO);
        } catch (Exception e) {
            String msg = String.format("Exceção: %s", e.getMessage());
            throw new RuntimeException(msg);
        }

    }
}
