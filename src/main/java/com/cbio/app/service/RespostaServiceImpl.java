package com.cbio.app.service;

import com.cbio.app.command.CommandStrategy;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.repository.ComandoRepository;
import com.cbio.app.repository.SessaoRepository;
import com.cbio.app.service.enuns.EtapaPadraoEnum;
import com.cbio.app.service.mapper.ComandoMapper;
import com.cbio.app.service.mapper.CycleAvoidingMappingContext;
import com.cbio.core.service.RespostaService;
import com.cbio.core.v1.dto.ComandoDTO;
import com.cbio.core.v1.dto.DecisaoResposta;
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
        ApplicationContext context,
        SessaoRepository sessaoRepository,
        ComandoRepository comandoRepository,
        ComandoMapper comandoMapper
    ) implements RespostaService {

    @Override
    public List<DecisaoResposta> decidirResposta(String texto, String ultimaEtapa, SessaoEntity sessao) {

        if(texto!= null) {
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
                return repostaPadrao();
            }
        } else {
            return repostaPadrao();
        }

    }

    List<DecisaoResposta> repostaPadrao() {
        return List.of(new DecisaoResposta("Digite o que deseja fazer.", ""));
    }


    List<DecisaoResposta> resolveDecisao(String texto, String proximaEtapa) {
        return List.of(new DecisaoResposta(texto, proximaEtapa));
    }
}
