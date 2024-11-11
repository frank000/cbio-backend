package com.cbio.app.service.assitents;

import com.cbio.app.client.RasaClient;
import com.cbio.app.client.RasaRestClient;
import com.cbio.app.service.enuns.AssistentEnum;
import com.cbio.core.service.AssistentBotService;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.core.service.CompanyService;
import com.cbio.core.v1.dto.RasaMessageDTO;
import com.cbio.core.v1.dto.outchatmessages.RasaMessageOutDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service("rasaAssistent")
@RequiredArgsConstructor
public class RasaAssistent implements AssistentBotService {

    private final RasaClient rasaClient;
    private final RasaRestClient rasaRestClient;
    private final CompanyService companyService;

    public Optional<DialogoDTO> processaDialogoAssistent(DialogoDTO dialogo) {

        try {
            RasaMessageOutDTO rasaMessageDTO = RasaMessageOutDTO
                    .builder()
                    .mensagem(dialogo.getMensagem())
                    .identificadorRemetente(dialogo.getIdentificadorRemetente().concat("_").concat(dialogo.getCanal().getIdCanal()))
                    .build();

            Integer port = companyService.getPortByIdCompany(dialogo.getCanal().getCompany().getId());

            List<RasaMessageDTO> webhook = Arrays.asList(rasaRestClient.sendMessage(rasaMessageDTO, port));

            List<DialogoDTO> respostas = webhook
                    .stream()
                    .map(o ->
                            {
                                return DialogoDTO.builder()
                                        .mensagem(o.getText())
                                        .type(StringUtils.hasText(o.getText())? "TEXT" : null)
                                        .buttons(getButtons(o))
                                        .from(AssistentEnum.RASA.name())
                                        .identificadorRemetente(o.getIdentificadorId())
                                        .sessionId(dialogo.getSessionId())
                                        .build();
                            }
                    )
                    .collect(Collectors.toList());

            if(respostas.isEmpty()){
                log.info("ASSISTENT-BOT-RASA: Não obtivemos respostas");
            }
            return respostas.stream().findFirst();

        } catch (Exception e) {
            String msg = String.format("Envio para o Assistente Rasa com problema: %s", e.getMessage());
            log.error(msg);
//            throw new RuntimeException(msg);
            return null;
        }

    }

    @NotNull
    private static List<RasaMessageDTO.Button> getButtons(RasaMessageDTO o) {
        return o.getButtons() != null && !o.getButtons().isEmpty() ? o.getButtons() : Collections.emptyList();
    }
}
