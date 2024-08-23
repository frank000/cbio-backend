package com.cbio.app.service;

import com.cbio.core.service.ChatbotForwardService;
import com.cbio.core.service.SessaoService;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.service.assitents.AttendantAssistent;
import com.cbio.app.service.assitents.RasaAssistent;
import com.cbio.app.service.enuns.AssistentEnum;
import com.cbio.app.service.enuns.CanalSenderEnum;
import com.cbio.app.service.serder.Sender;
import com.cbio.core.service.AssistentBotService;
import com.cbio.core.v1.dto.DialogoDTO;
import com.cbio.core.v1.dto.EntradaMensagemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatbotForwardServiceImpl implements ChatbotForwardService {

    private final ApplicationContext applicationContext;


    private final SessaoService sessaoService;


    @Override
    public void processaMensagem(EntradaMensagemDTO entradaMensagemDTO){

        DialogoDTO dialogoDTO = DialogoDTO.builder()
                .mensagem(entradaMensagemDTO.getMensagem())
                .identificadorRemetente(entradaMensagemDTO.getIdentificadorRemetente())
                .canal(entradaMensagemDTO.getCanal())
                .build();

        SessaoEntity sessaoEntity = sessaoService.validaOuCriaSessaoAtivaPorUsuarioCanal(
                Long.valueOf(dialogoDTO.getIdentificadorRemetente()),
                entradaMensagemDTO.getCanal().getNome(),
                System.currentTimeMillis()
        );

        AssistentBotService assistentBotService;

        if(Boolean.TRUE.equals(sessaoEntity.getAtendimentoAberto())){
            assistentBotService = (AttendantAssistent)applicationContext.getBean(AssistentEnum.ATTENDANT.getBeanName());
        }else{
            assistentBotService = (RasaAssistent)applicationContext.getBean(AssistentEnum.RASA.getBeanName());
        }


        assistentBotService.processaDialogoAssistent(dialogoDTO)
                .filter(dialogoDTO1 -> isNotCommand(dialogoDTO1))
                .ifPresent(resposta -> {
                    resposta.setCanal(entradaMensagemDTO.getCanal());

                    CanalSenderEnum canalSenderEnum = CanalSenderEnum.valueOf(entradaMensagemDTO.getCanal().getNome());
                    Sender senderService = (Sender) applicationContext.getBean(canalSenderEnum.getCanalSender());
                    senderService.envia(resposta);
                });


    }

    private static boolean isNotCommand(DialogoDTO dialogoDTO1) {
        return !dialogoDTO1.getMensagem().startsWith("/");
    }
}
