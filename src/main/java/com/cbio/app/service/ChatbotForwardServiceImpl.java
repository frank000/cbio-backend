package com.cbio.app.service;

import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.service.assitents.AttendantAssistent;
import com.cbio.app.service.assitents.RasaAssistent;
import com.cbio.app.service.enuns.AssistentEnum;
import com.cbio.app.service.enuns.CanalSenderEnum;
import com.cbio.app.service.minio.MinioService;
import com.cbio.app.service.serder.Sender;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.service.AssistentBotService;
import com.cbio.core.service.ChatbotForwardService;
import com.cbio.core.service.DialogoService;
import com.cbio.core.service.SessaoService;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.EntradaMensagemDTO;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatbotForwardServiceImpl implements ChatbotForwardService {

    private static final Logger log = LoggerFactory.getLogger(ChatbotForwardServiceImpl.class);
    private final ApplicationContext applicationContext;

    private final SessaoService sessaoService;

    private final MinioService minioService;

    private final DialogoService dialogoService;

    @Override
    public void processaMensagem(EntradaMensagemDTO entradaMensagemDTO) throws Exception {

        boolean hasUuid = entradaMensagemDTO.getUuid() != null;//true if came from whatsapp
        if(hasUuid && dialogoService.hasDialogByUuid(entradaMensagemDTO.getUuid())){
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        SessaoEntity sessaoEntity = sessaoService.validaOuCriaSessaoAtivaPorUsuarioCanal(
                Long.valueOf(entradaMensagemDTO.getIdentificadorRemetente()),
                entradaMensagemDTO.getCanal(),
                System.currentTimeMillis()
        );

        DialogoDTO.DialogoDTOBuilder dialogoDTOBuilder = DialogoDTO.builder()
                .mensagem(entradaMensagemDTO.getMensagem())
                .identificadorRemetente(entradaMensagemDTO.getIdentificadorRemetente())
                .media(entradaMensagemDTO.getMedia())
                .canal(entradaMensagemDTO.getCanal())
                .type(entradaMensagemDTO.getType())
                .createdDateTime(now)
                .sessionId(sessaoEntity.getId())
                .uuid(entradaMensagemDTO.getUuid())
                .channelUuid((sessaoEntity.getLastChannelChat() != null) ? sessaoEntity.getLastChannelChat().getChannelUuid() : null);

        if (sessaoEntity.getUltimoAtendente() != null && Boolean.TRUE.equals(sessaoEntity.getAtendimentoAberto())) {
            dialogoDTOBuilder.toIdentifier(ObjectUtils.defaultIfNull(sessaoEntity.getUltimoAtendente().getId(), null));
        }

        if (Boolean.TRUE.equals(sessaoEntity.getAtendimentoAberto())) {
            dialogoDTOBuilder.sessionId(sessaoEntity.getId());
        }

        AssistentBotService assistentBotService;

        if (Boolean.TRUE.equals(sessaoEntity.getAtendimentoAberto())) {
            assistentBotService = (AttendantAssistent) applicationContext.getBean(AssistentEnum.ATTENDANT.getBeanName());
        } else {
            assistentBotService = (RasaAssistent) applicationContext.getBean(AssistentEnum.RASA.getBeanName());
        }

        DialogoDTO dialogoDTO = dialogoDTOBuilder.build();
        try {
            putFile(entradaMensagemDTO, sessaoEntity);

            DialogoDTO dialogoSaved = dialogoService.saveDialogo(dialogoDTO);

            assistentBotService
                    .processaDialogoAssistent(dialogoSaved)
                    .filter(ChatbotForwardServiceImpl::isNotCommand)
                    .ifPresent(resposta -> {

                        resposta.setCanal(entradaMensagemDTO.getCanal());
                        enviaRespostaDialogoPorCanal(entradaMensagemDTO.getCanal(), resposta);

                    });
        } catch (Exception e) {
            log.error("SAVE DIALOG - {}", e.getMessage());
            throw new RuntimeException(e);
        }


    }

    private void putFile(EntradaMensagemDTO entradaMensagemDTO, SessaoEntity sessaoEntity) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if(entradaMensagemDTO.getFile()!=null){
            minioService.putFile(entradaMensagemDTO.getFile(), entradaMensagemDTO.getMedia().getId(), sessaoEntity.getLastChannelChat().getChannelUuid());
        }
    }


    @Transactional(rollbackFor = RuntimeException.class)
    public DialogoDTO enviaRespostaDialogoPorCanal(CanalDTO canal, DialogoDTO dialogoResposta) {
        try{

            CanalSenderEnum canalSenderEnum = CanalSenderEnum.valueOf(canal.getNome().toUpperCase().trim());
            Sender senderService = (Sender) applicationContext.getBean(canalSenderEnum.getCanalSender());
            dialogoResposta.setCreatedDateTime(LocalDateTime.now());


            DialogoDTO dialogoDTO = dialogoService.saveDialogo(dialogoResposta);
            dialogoResposta.setId(dialogoDTO.getId());
            senderService.envia(dialogoResposta);
            return dialogoResposta;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    private static boolean isNotCommand(DialogoDTO dialogoDTO1) {
        return !dialogoDTO1.getMensagem().startsWith("/");
    }

    public Optional<DialogoDTO> notifyUserClosingAttendance(String mensagem, String channelId, SessaoEntity sessaoEntity) {

        DialogoDTO dialogoDTO = DialogoDTO.builder()
                .mensagem(mensagem)
                .identificadorRemetente(String.valueOf(sessaoEntity.getIdentificadorUsuario()))
                .toIdentifier(sessaoEntity.getId())
                .canal(sessaoEntity.getCanal())
                .type("TEXT")
                .media(null)
                .from(AssistentEnum.ATTENDANT.name())
                .channelUuid(channelId)
                .createdDateTime(LocalDateTime.now())
                .build();

        DialogoDTO dialogoDTO1 = enviaRespostaDialogoPorCanal(sessaoEntity.getCanal(), dialogoDTO);
        AttendantAssistent bean = (AttendantAssistent) applicationContext.getBean(AssistentEnum.ATTENDANT.getBeanName());
        return bean.processaDialogoAssistent(dialogoDTO1);
    }

}
