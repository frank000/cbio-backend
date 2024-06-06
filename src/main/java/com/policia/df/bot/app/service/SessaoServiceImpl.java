package com.policia.df.bot.app.service;

import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.app.repository.SessaoRepository;
import com.policia.df.bot.core.service.SessaoService;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Base64;
import java.util.UUID;

@Service
@Data
public class SessaoServiceImpl implements SessaoService {

    private final SessaoRepository repository;

    @Override
    public SessaoEntity validateSession(Update update, Long agora) {

        Long expiresValue = 120000L;

        UUID sessaoIdTemp = UUID.randomUUID();

        SessaoEntity sessao = repository.findByUsuarioAndAtivo(update.getMessage().getFrom().getId(), true);

        if(sessao == null) {
            sessao = new SessaoEntity();

            sessao.setSessaoId(sessaoIdTemp);
            sessao.setInicioSessao(agora);
            sessao.setAtivo(true);
            sessao.setExpiresAt(agora + expiresValue); // 2 minutos
            sessao.setUsuario(update.getMessage().getFrom().getId());

            return repository.save(sessao);

        } else { //expirou
            if(agora > sessao.getExpiresAt()) {

                sessao.setAtivo(false);
                sessao.setFinalSessao(agora);

            } else { //renova
                sessao.setExpiresAt(agora + expiresValue); // 2 minutos
            }

            return repository.save(sessao);

        }

    }

    @Override
    public Boolean sessaoValida(Long agora, SessaoEntity sessao) {
        return agora <= sessao.getExpiresAt();
    }

    @Override
    public void atualizarSessao(SessaoEntity sessao, String ultimaAcao) {
        sessao.setUltimaAcao(ultimaAcao);
        repository.save(sessao);
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
