package com.policia.df.bot.app.service;

import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.app.repository.SessaoRepository;
import com.policia.df.bot.core.service.SessaoService;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Base64;
import java.util.UUID;

@Service
@Data
public class SessaoServiceImpl implements SessaoService {

    private final SessaoRepository repository;

    @Override
    public SessaoEntity validaOuCriaSessaoAtivaPorUsuario(Long usuarioId, Long agora) {

        Long expiresValue = 120000L;

        SessaoEntity sessao = repository.findByAtivoAndUsuario(Boolean.TRUE, usuarioId);

        if(sessao == null) {

            return criaESalvaSessao(usuarioId, agora, expiresValue);

        } else {

            return expiraOuRenovaSessao(agora, sessao, expiresValue);
        }

    }

    private @NotNull SessaoEntity expiraOuRenovaSessao(Long agora, SessaoEntity sessao, Long expiresValue) {
        if(agora > sessao.getExpiresAt()) {

            sessao.setAtivo(Boolean.FALSE);
            sessao.setFinalSessao(agora);

        } else { //renova
            sessao.setExpiresAt(agora + expiresValue); // 2 minutos
        }

        return repository.save(sessao);
    }

    private @NotNull SessaoEntity criaESalvaSessao(Long usuarioId, Long agora, Long expiresValue) {
        SessaoEntity sessao;
        sessao = SessaoEntity.builder()
                .sessaoId(UUID.randomUUID())
                .usuario(usuarioId)
                .inicioSessao(agora)
                .ativo(Boolean.TRUE)
                .expiresAt(agora + expiresValue)
                .build();

        return repository.save(sessao);
    }

    @Override
    public Boolean isSessaoValidaTempo(Long agora, SessaoEntity sessao) {
        return agora <= sessao.getExpiresAt();
    }

    @Override
    public void atualizarSessao(SessaoEntity sessao, String ultimaAcao) {
        sessao.setUltimaEtapa(ultimaAcao);
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
