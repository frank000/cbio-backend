package com.policia.df.bot.app.command;

import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.app.repository.SessaoRepository;
import com.policia.df.bot.app.service.enuns.EtapaPadraoEnum;
import com.policia.df.bot.core.service.KeycloakService;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@Component("otpCommand")
@Getter
@Slf4j
public class OtpCommand implements CommandStrategy {

    private final SessaoRepository sessaoRepository;
    public Map<String, BiFunction<String, SessaoEntity, DecisaoResposta>> relacaoDeEtapasEFuncoes = new HashMap<>();

    private final KeycloakService keycloakService;

    public OtpCommand(KeycloakService keycloakService, SessaoRepository sessaoRepository) {
        this.keycloakService = keycloakService;

        relacaoDeEtapasEFuncoes.put(EtapaPadraoEnum.INIT.getValor(), (texto, sessao) -> resolveDecisao("Por favor, digite o nome de usuário. Ex: fulano.ciclano", "step_otp"));
        relacaoDeEtapasEFuncoes.put("step_otp", this::keycloak);
        this.sessaoRepository = sessaoRepository;
    }


    @Override
    public Map<String, BiFunction<String, SessaoEntity, DecisaoResposta>> getFuncaoEtapas() {
        return relacaoDeEtapasEFuncoes;
    }


    public DecisaoResposta keycloak(String texto, SessaoEntity sessao) {

        Optional<List<UserRepresentation>> listUsuarios = keycloakService.pesquisarUsuario(texto.toLowerCase());

        if(listUsuarios.isPresent()){

            try {
                UserRepresentation userRepresentation = listUsuarios.get()
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new Exception("Usuário não encontrado no Keycloak."));

                keycloakService.deletarUsuario(userRepresentation.getId().toLowerCase());
                sessao.flush(sessaoRepository);

                return resolveDecisao(texto + " - OTP resetado com sucesso.", "");
            } catch (Exception e) {

                log.error("Erro na busca do usuário no keycloak. Erro ", e);
                return resolveDecisao("Erro ao deletar usuário. Informe novamente.", "step_otp");
            }
        }else{

            return resolveDecisao("Usuário não encontrado. Informe novamente.", "step_otp");

        }


    }

}
