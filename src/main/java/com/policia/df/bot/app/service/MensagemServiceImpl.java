package com.policia.df.bot.app.service;

import com.policia.df.bot.app.entities.MensagemEntity;
import com.policia.df.bot.app.repository.MensagemRepository;
import com.policia.df.bot.core.service.MensagemService;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Data
public class MensagemServiceImpl implements MensagemService {

    private final MensagemRepository repository;

    @Override
    public void salvarMensagem(Message message) {

        MensagemEntity mensagem = new MensagemEntity();

        mensagem.setCanalId(message.getFrom().getId());
//        mensagem.se

        repository.save(mensagem);

    }
}
