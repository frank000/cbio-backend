package com.cbio.app.service;

import com.cbio.app.entities.MensagemEntity;
import com.cbio.app.repository.MensagemRepository;
import com.cbio.core.service.MensagemService;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Data
public class MensagemServiceImpl implements MensagemService {

    private final MensagemRepository repository;

    @Override
    public void salvarMensagem(Update update, Long identificadorCanal, String sessao) {

        MensagemEntity mensagem = new MensagemEntity();

        mensagem.setCanalId(identificadorCanal);
        mensagem.setSessao("");
        mensagem.setText(update.getMessage().getText());
        mensagem.setChatId(update.getMessage().getChatId());
        mensagem.setTimestamp(Long.parseLong(update.getMessage().getDate().toString()));
        mensagem.setMessagemId(Long.parseLong(update.getMessage().getMessageId().toString()));
        mensagem.setUsuarioId(update.getMessage().getFrom().getId());
        mensagem.setSessao(sessao);

//        System.out.println("<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>" + update.getChannelPost().getText());

        repository.save(mensagem);

    }
}
