package com.policia.df.bot.app.command;

import com.policia.df.bot.app.entities.EtapaEntity;
import com.policia.df.bot.app.entities.SessaoEntity;
import com.policia.df.bot.app.repository.EtapaRepository;
import com.policia.df.bot.app.repository.SessaoRepository;
import com.policia.df.bot.app.service.enuns.EtapaPadraoEnum;
import com.policia.df.bot.core.service.KeycloakService;
import com.policia.df.bot.core.v1.dto.DecisaoResposta;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

@Component("otpCommand")
@Getter
@Slf4j
public class OtpCommand implements CommandStrategy {

    private final SessaoRepository sessaoRepository;
    public Map<String, BiFunction<String, SessaoEntity, List<DecisaoResposta>>> relacaoDeEtapasEFuncoes = new HashMap<>();

    private final KeycloakService keycloakService;

    private final EtapaRepository etapaRepository;

    public OtpCommand(KeycloakService keycloakService, SessaoRepository sessaoRepository, EtapaRepository etapaRepository) {

        this.keycloakService = keycloakService;
        this.sessaoRepository = sessaoRepository;
        this.etapaRepository = etapaRepository;

    }

    @PostConstruct
    private void init() {

        List<EtapaEntity> listaEtapas = etapaRepository.findAllByComandoEtapa("otpCommand");

        listaEtapas.forEach(e -> {
            if("texto".equalsIgnoreCase(e.getTipoEtapa())) {

                relacaoDeEtapasEFuncoes.put(e.getNomeEtapa(), (texto, sessao) -> List.of(resolveDecisao(e.getMensagemEtapa(), e.getProximaEtapa())));

            } else {

                relacaoDeEtapasEFuncoes.put(e.getNomeEtapa(), this::keycloak);

            }
        });
    }

    @Override
    public Map<String, BiFunction<String, SessaoEntity, List<DecisaoResposta>>> getFuncaoEtapas() {
        return relacaoDeEtapasEFuncoes;
    }


    public List<DecisaoResposta> keycloak(String texto, SessaoEntity sessao) {

        if(!isUsuarioDigitadoCorretamente(texto)) {

            return List.of(resolveDecisao("Usuário digitado incorretamente. O usuário possui o seguinte padrão: ''fulano.ciclano''.", "step_otp"));
        }

        Optional<List<UserRepresentation>> listUsuarios = keycloakService.pesquisarUsuario(texto.toLowerCase());

        if(listUsuarios.isPresent()){

            try {
                UserRepresentation userRepresentation = listUsuarios.get()
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new Exception("Usuário não encontrado no Keycloak."));

                keycloakService.deletarUsuario(userRepresentation.getId().toLowerCase());
                sessao.flush(sessaoRepository);

                return List.of(resolveDecisao(texto + " - OTP resetado com sucesso.", ""));
            } catch (Exception e) {

                log.error("Erro na busca do usuário no keycloak. Erro ", e);
                return List.of(resolveDecisao("Erro ao deletar usuário. Informe novamente.", "step_otp"));
            }
        } else {

            return List.of(resolveDecisao("Usuário não encontrado. Informe novamente.", "step_otp"));

        }
    }

    private Boolean isUsuarioDigitadoCorretamente(String usuario) {

        Pattern pattern = Pattern.compile("\\w+\\.\\w+", Pattern.CASE_INSENSITIVE);

        return pattern.matcher(usuario).find();
    }

}
