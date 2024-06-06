package com.policia.df.bot.app.service;

import com.policia.df.bot.core.service.KeycloakService;
import com.policia.df.bot.core.service.RespostaService;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public record RespostaServiceImpl(KeycloakService keycloakService) implements RespostaService {

    @Override
    public DecisaoResposta decidirResposta(String texto, String ultimaAcao) {

        Pattern patternUsername = Pattern.compile("[*a-z]\\.[*a-z]", Pattern.CASE_INSENSITIVE);

        Pattern patternOtp = Pattern.compile("otp", Pattern.CASE_INSENSITIVE);

        Pattern patternReset = Pattern.compile("/reset", Pattern.CASE_INSENSITIVE);

        if(patternReset.matcher(texto.trim()).find()) {
            return resolveDecisao("Serviço reiniciado", "");
        }

        if(patternOtp.matcher(texto.trim()).find() && !"step_otp".equals(ultimaAcao)) {
            return resolveDecisao("Por favor, digite o nome de usuário. Ex: fulano.ciclano", "step_otp");
        }

        if(!"step_otp".equals(ultimaAcao)) {
            return resolveDecisao("Digite o que deseja fazer.", "");
        }

        if(patternUsername.matcher(texto.trim()).find() && "step_otp".equals(ultimaAcao)) {

            List<UserRepresentation> user = keycloakService.pesquisarUsuario(texto.toLowerCase());

            if(user != null) {

                try {

                    keycloakService.deletarUsuario(user.get(0).getId().toLowerCase());

                    return resolveDecisao(texto + " - OTP resetado com sucesso.", "");

                } catch (Exception e) {

                    return resolveDecisao("Erro ao deletar usuário. Informe novamente.", "step_otp");

                }

            } else {

                return resolveDecisao("Usuário não encontrado. Informe novamente.", "step_otp");

            }

        } else {

            return resolveDecisao("Verifique se o usuário está correto e informe novamente.", "step_otp");

        }

    }

    DecisaoResposta resolveDecisao(String texto, String acao) {
        return new DecisaoResposta(texto, acao);
    }
}
