package com.policia.df.bot.app.command;

import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.core.service.KeycloakService;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;
import lombok.Getter;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
@Component("otpCommand")
@Getter
public class OtpCommand implements CommandStrategy {

    public Map<String, BiFunction<String, SessaoEntity, DecisaoResposta>> relacaoDeEtapasEFuncoes = new HashMap<>();

    private final KeycloakService keycloakService;

    public OtpCommand(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;

        relacaoDeEtapasEFuncoes.put("init", (texto, sessao) -> resolveDecisao("Por favor, digite o nome de usuário. Ex: fulano.ciclano", "step_otp"));
        relacaoDeEtapasEFuncoes.put("step_otp", this::keycloak);
    }


    @Override
    public Map<String, BiFunction<String, SessaoEntity, DecisaoResposta>> getFun() {
        return relacaoDeEtapasEFuncoes;
    }



    public DecisaoResposta keycloak(String texto, SessaoEntity sessao) {

        List<UserRepresentation> user = keycloakService.pesquisarUsuario(texto.toLowerCase());

        if (user != null) {

            try {

                keycloakService.deletarUsuario(user.get(0).getId().toLowerCase());

                return resolveDecisao(texto + " - OTP resetado com sucesso.", "");

            } catch (Exception e) {

                return resolveDecisao("Erro ao deletar usuário. Informe novamente.", "step_otp");

            }

        } else {

            return resolveDecisao("Usuário não encontrado. Informe novamente.", "step_otp");

        }

    }

}
