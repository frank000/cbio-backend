package com.cbio.rasa.action;

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
import com.cbio.core.service.AttendantService;
import com.cbio.core.service.SessaoService;
import com.cbio.core.v1.dto.UsuarioDTO;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
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
import org.springframework.ai.document.Document;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class RagQueryAction implements Action {

    private final SessaoService sessaoService;
    private final AttendantService attendantService;
    private final ChatService chatService;
    private final ChatChannelRepository chatChannelRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final CompanyConfigRepository companyConfigRepository;


    @Override
    public String name() {
        return "ac_rag_query";
    }

    @Override
    public List<? extends Event> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) throws RejectExecuteException {
        log.info("ACTION: ac_rag_query");
        String text = tracker.getLatestMessage().getText();

        if (tracker.getCurrentState() != null && tracker.getCurrentState().getSenderId() != null) {

            String[] idUsuarioAndIdCanal = tracker.getCurrentState().getSenderId().split("_");

            SessaoEntity sessaoEntity = sessaoService
                    .buscaSessaoAtivaPorIdentificadorUsuario(Long.valueOf(idUsuarioAndIdCanal[0]), idUsuarioAndIdCanal[1]);

            try {
                String idCompany = sessaoEntity.getCanal().getCompany().getId();
                Optional<CompanyConfigEntity> byCompanyId = companyConfigRepository.findByCompanyId(idCompany);


                if (byCompanyId.isPresent()) {
                    AllMiniLmL6V2EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
                    InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

                    int maxSegmentSizeInChars = 500; // Máximo de 500 caracteres por parágrafo.
                    int maxOverlapSizeInChars = 50;  // Sobreposição de até 50 caracteres.

                    List<Document> list = new ArrayList<>();
                    byCompanyId.get()
                            .getRag()
                            .forEach(s -> {

                                DocumentByParagraphSplitter splitter = new DocumentByParagraphSplitter(maxSegmentSizeInChars, maxOverlapSizeInChars);
                                String[] split1 = splitter.split(s);

                                List<TextSegment> split = Arrays.stream(split1)
                                        .map(s1 -> new TextSegment(s1, new Metadata()))
                                        .toList();
                                split
                                        .forEach(textSegment -> {
                                            Embedding embedding1 = embeddingModel.embed(textSegment).content();
                                            embeddingStore.add(embedding1, textSegment);

                                        });
                            });

                    Embedding queryEmbedding = embeddingModel
                            .embed(text)
                            .content();

                    EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                            .maxResults(1)
                            .minScore(0.72)
                            .queryEmbedding(queryEmbedding)
                            .build();

                    EmbeddingSearchResult<TextSegment> search = embeddingStore.search(searchRequest);

                    List<EmbeddingMatch<TextSegment>> relevant = search.matches();
                    String resposta;
                    if (CollectionUtils.isEmpty(relevant)) {
                        resposta = "Desculpe, não consegui. Refaça a pergunta ou seja mais específico.";
                    } else {
                        resposta = relevant.get(0).embedded().text();
                    }

                    dispatcher
                            .utterMessage(Message
                                    .builder()
                                    .text(resposta)
                                    .build());

                }
                dispatcher
                        .utterMessage(Message
                                .builder()
                                .text(text)
                                .build());

            } catch (Exception e) {
                e.printStackTrace();
                text = e.getMessage();
            }

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

        if (Boolean.TRUE.equals(companyConfigEntity.getKeepSameAttendant())) {
            attendantDTO = sessaoEntity.getUltimoAtendente();

            if (attendantDTO == null ||
                    !attendantService.isAttendantActive(attendantDTO.getId())) {
                attendantDTO = getAttendantByLessAttendance(companyId);
            }
        } else {
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