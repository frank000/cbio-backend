package com.policia.df.bot.app.service;

import com.policia.df.bot.app.command.CommandStrategy;
import com.policia.df.bot.app.command.OtpCommand;
import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.app.repository.SessaoRepository;
import com.policia.df.bot.core.service.KeycloakService;
import com.policia.df.bot.core.service.RespostaService;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public record RespostaServiceImpl(
        KeycloakService keycloakService,
        ApplicationContext context,
        SessaoRepository sessaoRepository) implements RespostaService {

    @Override
    public DecisaoResposta decidirResposta(String texto, String ultimaEtapa, SessaoEntity sessao) {



        Map<String, String> listaComandos = new HashMap<>();
        listaComandos.put("otp", "otpCommand");
        listaComandos.put("reset", "resetCommand");

        Pattern patternOtp = Pattern.compile("\\/[*a-z]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patternOtp.matcher(texto);

        boolean isComando = matcher.find();

        if(isComando && listaComandos.containsKey(matcher.group(0).replace("/", ""))) {
            String commandName = matcher.group(0).replace("/", "");

            sessao.setUltimoComando(commandName);
            sessaoRepository.save(sessao);

            CommandStrategy commandStrategy = (CommandStrategy)context.getBean(listaComandos.get(commandName));

            boolean contemEtapa = commandStrategy
                    .getFun()
                    .containsKey(ultimaEtapa);

            String etapaAExecutar = !contemEtapa? "init" : ultimaEtapa;


            return commandStrategy
                    .getFun()
                    .get(etapaAExecutar)
                    .apply(texto, sessao);

        }else if(StringUtils.hasText(sessao.getUltimoComando())){

            CommandStrategy commandStrategy = (CommandStrategy)context.getBean(listaComandos.get(sessao.getUltimoComando()));

            return commandStrategy
                    .getFun()
                    .get(ultimaEtapa)
                    .apply(texto, sessao);

        }else{
            return resolveDecisao("Digite o que deseja fazer.", "");
        }
    }

    DecisaoResposta resolveDecisao(String texto, String acao) {
        return new DecisaoResposta(texto, acao);
    }
}
