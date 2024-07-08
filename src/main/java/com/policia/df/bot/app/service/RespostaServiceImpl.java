package com.policia.df.bot.app.service;

import com.policia.df.bot.app.command.CommandStrategy;
import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.app.repository.ComandoRepository;
import com.policia.df.bot.app.repository.SessaoRepository;
import com.policia.df.bot.app.service.enuns.EtapaPadraoEnum;
import com.policia.df.bot.app.service.mapper.ComandoMapper;
import com.policia.df.bot.app.service.mapper.CycleAvoidingMappingContext;
import com.policia.df.bot.core.service.KeycloakService;
import com.policia.df.bot.core.service.RespostaService;
import com.policia.df.bot.core.v1.dto.ComandoDTO;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public record RespostaServiceImpl(
        KeycloakService keycloakService,
        ApplicationContext context,
        SessaoRepository sessaoRepository,
        ComandoRepository comandoRepository,
        ComandoMapper comandoMapper
    ) implements RespostaService {

    @Override
    public DecisaoResposta decidirResposta(String texto, String ultimaEtapa, SessaoEntity sessao) {

        Map<String, String> listaComandos = new HashMap<>();

        List<ComandoDTO> listaComandosRepo =
                comandoMapper.listComandoEntityToListComandoDTO(comandoRepository.findAllByAtivo(Boolean.TRUE), new CycleAvoidingMappingContext());

        listaComandosRepo.forEach(e -> {
            listaComandos.put(e.getNome(), e.getNomeServico());
        });

        Pattern patternComando = Pattern.compile("\\/[*a-z]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patternComando.matcher(texto);

        boolean isComando = matcher.find();

        if(isComando && listaComandos.containsKey(matcher.group(0).replace("/", ""))) {
            String commandName = matcher.group(0).replace("/", "");

            sessao.setComandoExecucao(commandName);
            sessaoRepository.save(sessao);

            CommandStrategy commandStrategy = (CommandStrategy)context.getBean(listaComandos.get(commandName));

            boolean contemEtapa = commandStrategy
                    .getFuncaoEtapas()
                    .containsKey(ultimaEtapa);

            String etapaAExecutar = !contemEtapa? EtapaPadraoEnum.INIT.getValor() : ultimaEtapa;

            return commandStrategy
                    .getFuncaoEtapas()
                    .get(etapaAExecutar)
                    .apply(texto, sessao);

        } else if(StringUtils.hasText(sessao.getComandoExecucao())) {

            CommandStrategy commandStrategy = (CommandStrategy)context.getBean(listaComandos.get(sessao.getComandoExecucao()));

            return commandStrategy
                    .getFuncaoEtapas()
                    .get(ultimaEtapa)
                    .apply(texto, sessao);

        } else {
            return resolveDecisao("Digite o que deseja fazer.", "");
        }
    }

    DecisaoResposta resolveDecisao(String texto, String proximaEtapa) {
        return new DecisaoResposta(texto, proximaEtapa);
    }
}
