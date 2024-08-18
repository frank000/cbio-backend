package com.policia.df.bot.app.service;

import com.policia.df.bot.app.service.enuns.CanalSenderEnum;
import com.policia.df.bot.app.service.serder.Sender;
import com.policia.df.bot.core.service.AssistentBotService;
import com.policia.df.bot.core.service.ChatbotForwardService;
import com.policia.df.bot.core.v1.dto.DialogoDTO;
import com.policia.df.bot.core.v1.dto.EntradaMensagemDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatbotForwardServiceImpl implements ChatbotForwardService {

    private final ApplicationContext applicationContext;
    private final AssistentBotService assistentService;

    @Override
    public void processaMensagem(EntradaMensagemDTO entradaMensagemDTO){

        DialogoDTO dialogoDTO = DialogoDTO.builder()
                .mensagem(entradaMensagemDTO.getMensagem())
                .identificadorRemetente(entradaMensagemDTO.getIdentificadorRemetente())
                .canal(entradaMensagemDTO.getCanal())
                .build();

       assistentService.processaDialogoAssistent(dialogoDTO)
                .ifPresent(resposta -> {
                    resposta.setCanal(entradaMensagemDTO.getCanal());

                    CanalSenderEnum canalSenderEnum = CanalSenderEnum.valueOf(entradaMensagemDTO.getCanal().getNome());
                    Sender senderService = (Sender) applicationContext.getBean(canalSenderEnum.getCanalSender());
                    senderService.envia(resposta);
                });


    }
}
