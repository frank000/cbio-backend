package com.cbio.app.service;

import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.repository.SessaoRepository;
import com.cbio.core.service.SessaoService;
import com.cbio.core.v1.dto.CanalDTO;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@Data
public class SessaoServiceImpl implements SessaoService {

    private final SessaoRepository sessaoRepository;


    @Override
    public Optional<SessaoEntity> buscaSessaoUsuarioCanal(Long identificadorUsuario, String canal) {
        return sessaoRepository.findByCanalAndIdentificadorUsuario(canal, identificadorUsuario);
    }

    @Override
    public void salva(SessaoEntity sessao) {
        sessaoRepository.save(sessao);
    }

    @Override
    public SessaoEntity validaOuCriaSessaoAtivaPorUsuarioCanal(Long usuarioId, CanalDTO canal, Long agora){

        Long expiresValue = 120000L;

        Optional<SessaoEntity> byAtivoAndIdentificadorUsuarioAndCanal = sessaoRepository.findByAtivoAndIdentificadorUsuarioAndCanalNome(Boolean.TRUE, usuarioId, canal.getNome());

        return byAtivoAndIdentificadorUsuarioAndCanal
                .orElseGet(() -> criaESalvaSessao(usuarioId, agora, expiresValue, canal));



    }

    public SessaoEntity buscaSessaoAtivaPorUsuarioCanal(Long usuarioId, String canal, String channelId){
        return sessaoRepository.findByAtivoAndIdentificadorUsuarioAndCanalNomeAndChannelUuid(Boolean.TRUE, usuarioId, canal, channelId)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada."));

    }

    private @NotNull SessaoEntity expiraOuRenovaSessao(Long agora, SessaoEntity sessao, Long expiresValue) {
        if (agora > sessao.getExpiresAt()) {

            sessao.setAtivo(Boolean.FALSE);
            sessao.setFinalSessao(agora);

        } else { //renova
            sessao.setExpiresAt(agora + expiresValue); // 2 minutos
        }

        return sessaoRepository.save(sessao);
    }

    private @NotNull SessaoEntity criaESalvaSessao(Long usuarioId, Long agora, Long expiresValue, CanalDTO canal) {
        SessaoEntity sessao;
        sessao = SessaoEntity.builder()
                .sessaoId(UUID.randomUUID())
                .identificadorUsuario(usuarioId)
                .inicioSessao(agora)
                .ativo(Boolean.TRUE)
                .expiresAt(agora + expiresValue)
                .canal(canal)
                .build();

        return sessaoRepository.save(sessao);
    }

    @Override
    public Boolean isSessaoValidaTempo(Long agora, SessaoEntity sessao) {
        return agora <= sessao.getExpiresAt();
    }

    @Override
    public void atualizarSessao(SessaoEntity sessao, String ultimaAcao) {
        sessao.setUltimaEtapa(ultimaAcao);
        sessaoRepository.save(sessao);
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
