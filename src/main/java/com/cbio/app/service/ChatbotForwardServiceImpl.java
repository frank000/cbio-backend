package com.cbio.app.service;

import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.service.assitents.AttendantAssistent;
import com.cbio.app.service.assitents.RasaAssistent;
import com.cbio.app.service.enuns.AssistentEnum;
import com.cbio.app.service.enuns.CanalSenderEnum;
import com.cbio.app.service.serder.Sender;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.chat.models.ChatChannelEntity;
import com.cbio.chat.repositories.ChatChannelRepository;
import com.cbio.core.service.AssistentBotService;
import com.cbio.core.service.ChatbotForwardService;
import com.cbio.core.service.DialogoService;
import com.cbio.core.service.SessaoService;
import com.cbio.core.v1.dto.CanalDTO;
import com.cbio.core.v1.dto.EntradaMensagemDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatbotForwardServiceImpl implements ChatbotForwardService {

    private final ApplicationContext applicationContext;

    private final SessaoService sessaoService;

    private final DialogoService dialogoService;

    @Override
    public void processaMensagem(EntradaMensagemDTO entradaMensagemDTO){

        LocalDateTime now = LocalDateTime.now();
        SessaoEntity sessaoEntity = sessaoService.validaOuCriaSessaoAtivaPorUsuarioCanal(
                Long.valueOf(entradaMensagemDTO.getIdentificadorRemetente()),
                entradaMensagemDTO.getCanal(),
                System.currentTimeMillis()
        );

        DialogoDTO.DialogoDTOBuilder dialogoDTOBuilder = DialogoDTO.builder()
                .mensagem(entradaMensagemDTO.getMensagem())
                .identificadorRemetente(entradaMensagemDTO.getIdentificadorRemetente())
                .canal(entradaMensagemDTO.getCanal())
                .createdDateTime(now)
                .channelUuid((sessaoEntity.getLastChannelChat() != null) ? sessaoEntity.getLastChannelChat().getChannelUuid() : null);

        if(sessaoEntity.getUltimoAtendente() != null){
            dialogoDTOBuilder.toIdentifier(ObjectUtils.defaultIfNull(sessaoEntity.getUltimoAtendente().getId(), null));

        }

//
        if(Boolean.TRUE.equals(sessaoEntity.getAtendimentoAberto())) {
            dialogoDTOBuilder.sessionId(sessaoEntity.getId());
        }
//
//
        AssistentBotService assistentBotService;

        if(Boolean.TRUE.equals(sessaoEntity.getAtendimentoAberto())){
            assistentBotService = (AttendantAssistent)applicationContext.getBean(AssistentEnum.ATTENDANT.getBeanName());
        }else{
            assistentBotService = (RasaAssistent)applicationContext.getBean(AssistentEnum.RASA.getBeanName());
        }

        DialogoDTO dialogoDTO = dialogoDTOBuilder.build();
        dialogoService.saveDialogo(dialogoDTO);

        assistentBotService.processaDialogoAssistent(dialogoDTO)
                .filter(dialogoDTO1 -> isNotCommand(dialogoDTO1))
                .ifPresent(resposta -> {

                    resposta.setCanal(entradaMensagemDTO.getCanal());
                    enviaRespostaDialogoPorCanal( entradaMensagemDTO.getCanal(), resposta);

                });


    }

    public void enviaRespostaDialogoPorCanal(CanalDTO canal, DialogoDTO dialogoResposta) {
        CanalSenderEnum canalSenderEnum = CanalSenderEnum.valueOf(canal.getNome());
        Sender senderService = (Sender) applicationContext.getBean(canalSenderEnum.getCanalSender());
        senderService.envia(dialogoResposta);
        dialogoService.saveDialogo(dialogoResposta);
    }

    private static boolean isNotCommand(DialogoDTO dialogoDTO1) {
        return !dialogoDTO1.getMensagem().startsWith("/");
    }
}
