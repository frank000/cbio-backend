package com.cbio.rasa.action.telegram;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.CompanyConfigEntity;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.repository.CompanyConfigRepository;
import com.cbio.chat.dto.ChatChannelInitializationDTO;
import com.cbio.chat.dto.WebsocketNotificationDTO;
import com.cbio.chat.exceptions.IsSameUserException;
import com.cbio.chat.exceptions.UserNotFoundException;
import com.cbio.chat.models.ChatChannelEntity;
import com.cbio.chat.repositories.ChatChannelRepository;
import com.cbio.chat.services.ChatService;
import com.cbio.chat.services.WebsocketPath;
import com.cbio.core.service.AttendantService;
import com.cbio.core.service.SessaoService;
import com.cbio.core.v1.dto.UsuarioDTO;
import io.github.jrasa.Action;
import io.github.jrasa.CollectingDispatcher;
import io.github.jrasa.domain.Domain;
import io.github.jrasa.event.Event;
import io.github.jrasa.exception.RejectExecuteException;
import io.github.jrasa.message.Message;
import io.github.jrasa.tracker.Tracker;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitAttendantTelegramAction implements Action {

    private final SessaoService sessaoService;
    private final AttendantService attendantService;
    private final ChatService chatService;
    private final ChatChannelRepository chatChannelRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final CompanyConfigRepository companyConfigRepository;

    @Override
    public String name() {
        return "ac_talk_attendant_telegram";
    }

//    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public List<? extends Event> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) throws RejectExecuteException {
        log.info("ACTION: ac_init_attendant");
        String text = "Você está sendo transferido para um atendente.";

        if (tracker.getCurrentState() != null && tracker.getCurrentState().getSenderId() != null) {

            String[] idUsuarioAndIdCanal = tracker.getCurrentState().getSenderId().split("_");

            SessaoEntity sessaoEntity = sessaoService
                    .buscaSessaoAtivaPorIdentificadorUsuario(Long.valueOf(idUsuarioAndIdCanal[0]), idUsuarioAndIdCanal[1]);

            try {
                LocalDateTime now = LocalDateTime.now();

                ResultConnectedChannel result = connectChatChannel(sessaoEntity, now);

                sessaoEntity.setLastChannelChat(
                        SessaoEntity.ChannelChatDTO.builder()
                                .channelUuid(result.channelUuid())
                                .dateTimeStart(now)
                                .build());

                sessaoEntity.setAtendimentoAberto(Boolean.TRUE);
                sessaoEntity.setUltimoAtendente(result.attendantDTO());
                sessaoEntity.setDataHoraAtendimentoAberto(now);
                sessaoService.salva(sessaoEntity);


                WebsocketNotificationDTO websocketDTO = WebsocketNotificationDTO.builder()
                        .userId(sessaoEntity.getId())
                        .channelId(result.channelUuid())
                        .cpf(sessaoEntity.getCpf())
                        .identificadorRemetente(String.valueOf(sessaoEntity.getIdentificadorUsuario()))
                        .name(StringUtils.hasText(sessaoEntity.getNome()) ? sessaoEntity.getNome() : null)
                        .nameCanal(sessaoEntity.getCanal().getNome())
                        .active(true)
                        .time(CbioDateUtils.getDateTimeFormated(now))
                        .preview("Cliente solicita atendimento")
                        .build();

                notifyByWebsocket(WebsocketPath.Constants.CHAT, result, websocketDTO, "Notificação websocket CHAT - ");

                notifyByWebsocket(WebsocketPath.Constants.NOTIFICATION, result, websocketDTO, "Notificação websocket NOTIFICATION - ");


            } catch (Exception e) {
                text = e.getMessage();
            }


            dispatcher
                    .utterMessage(Message
                            .builder()
                            .text(text)
                            .build());
        }

        return Action.empty();
    }

    private void notifyByWebsocket(String notification, ResultConnectedChannel result, WebsocketNotificationDTO websocketDTO, String x) {
        simpMessagingTemplate
                .convertAndSend(String.format(notification, result.attendantDTO().getId()),
                        websocketDTO);
        log.info(x + String.format(notification, result.attendantDTO().getId()) + " - " + websocketDTO);
    }

    @NotNull
    private ResultConnectedChannel connectChatChannel(SessaoEntity sessaoEntity, LocalDateTime now) throws IsSameUserException, UserNotFoundException {
        //observar se sessoã tem um i=ultimo attendant, se tiver, verifica se existe, se esta ativo, e seta para ele, se não entra aqui
        String companyId = sessaoEntity.getCanal().getCompany().getId();
        UsuarioDTO attendantDTO;
        CompanyConfigEntity companyConfigEntity = companyConfigRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new NotFoundException("Configuração não encontrada."));

        if(Boolean.TRUE.equals(companyConfigEntity.getKeepSameAttendant())){
            attendantDTO = sessaoEntity.getUltimoAtendente();

            if(attendantDTO == null ||
                    !attendantService.isAttendantActive(attendantDTO.getId())){
                attendantDTO = getAttendantByLessAttendance(companyId);
            }
        }else{
            attendantDTO = getAttendantByLessAttendance(companyId);
        }


        ChatChannelInitializationDTO chatChannelInitialization = ChatChannelInitializationDTO.builder()
                .userIdOne(attendantDTO.getId())
                .userIdTwo(sessaoEntity.getId())
                .initCanal(sessaoEntity.getCanal().getNome())
                .build();

        String channelUuid = chatService.establishChatSession(chatChannelInitialization, now);

        addHistoryOnChannel(now, channelUuid);

        attendantService.incrementTotalChatsReceived(attendantDTO);

        ResultConnectedChannel result = new ResultConnectedChannel(attendantDTO, channelUuid);
        return result;
    }

    private void addHistoryOnChannel(LocalDateTime now, String channelUuid) {
        ChatChannelEntity chatChannelEntity = chatChannelRepository.findById(channelUuid)
                .orElseThrow(() -> new NotFoundException("Channel não encontrado."));
        chatChannelEntity.addHistory(now);
        chatChannelRepository.save(chatChannelEntity);
    }

    private UsuarioDTO getAttendantByLessAttendance(String companyId) {
        return attendantService.findTopByOrderByTotalChatsDistribuidosAsc(companyId);
    }

    private record ResultConnectedChannel(UsuarioDTO attendantDTO, String channelUuid) {
    }
}