package com.cbio.rasa.action;

import com.cbio.app.entities.CompanyConfigEntity;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.chat.dto.ChatChannelInitializationDTO;
import com.cbio.chat.dto.WebsocketNotificationDTO;
import com.cbio.chat.exceptions.IsSameUserException;
import com.cbio.chat.exceptions.UserNotFoundException;
import com.cbio.chat.models.ChatChannelEntity;
import com.cbio.core.v1.dto.UsuarioDTO;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
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
public class FallbackAction implements Action {


    @Override
    public String name() {
        return "action_fallback";
    }

    @Override
    public List<? extends Event> run(CollectingDispatcher dispatcher, Tracker tracker, Domain domain) throws RejectExecuteException {
        log.info("ACTION: action_fallback");

        dispatcher.utterMessage(Message.builder()
                .text("Desculpe, não consegui identificar sua pergunta. Poderia reformular ou ser mais específico?")
                .build());
        return Action.empty();
    }

}