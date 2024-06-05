package com.policia.df.bot.app.service;

import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.app.repository.SessaoRepository;
import com.policia.df.bot.core.service.SessaoService;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Base64;

@Service
@Data
public class SessaoServiceImpl implements SessaoService {

    private final SessaoRepository repository;

    @Override
    public void createSession(Update update) {

        String sessaoIdTemp = encode64(update.getMessage().getFrom().getId().toString());

        SessaoEntity sessao = repository.findBySessaoIdAndAtivo(sessaoIdTemp, true);

        Long agora = System.currentTimeMillis();

        if(sessao == null) {
            sessao = new SessaoEntity();

            sessao.setSessaoId(sessaoIdTemp);
            sessao.setInicioSessao(agora);
            sessao.setAtivo(true);
            sessao.setExpiresAt(agora + 120000); // 2 minutos
            sessao.setUsuario(update.getMessage().getFrom().getId());

            repository.save(sessao);
        } else { //expirou
            if(agora > sessao.getExpiresAt()) {
                sessao.setSessaoId(sessaoIdTemp);
                sessao.setAtivo(false);
                sessao.setUsuario(update.getMessage().getFrom().getId());
                sessao.setFinalSessao(agora);
            } else { //renova
                sessao.setSessaoId(sessaoIdTemp);
                sessao.setExpiresAt(agora + 120000); // 2 minutos
            }

            repository.save(sessao);
        }

    }

    String encode64(String string) {
        return Base64.getEncoder().encodeToString(string.getBytes());
    }

    String decode64(String b64) {
        byte[] decodedBytes = Base64.getDecoder().decode(b64);
        String decodedString = new String(decodedBytes);

        return decodedString;
    }
}
