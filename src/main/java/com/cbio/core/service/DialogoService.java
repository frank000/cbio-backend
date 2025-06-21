package com.cbio.core.service;

import com.cbio.chat.dto.ChatDTO;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.chat.models.DialogoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DialogoService {

    DialogoDTO saveDialogo(DialogoDTO dialogo);

    DialogoDTO updateDialogo(DialogoDTO dialogo);

    DialogoDTO getById(String id);

    List<DialogoDTO> getAll();

    Page<DialogoEntity> getAllBySender(String identificadorRementente, Pageable pageable);

//    List<ChatDTO> mountChatFromDioalogBySessionIdAndChannelId(String sessionId, ChatChannelEntity chatChannelEntity);

    Page<ChatDTO> mountChatFromDioalogBySessionIdAndChannelIdPaginated(String sessionId, String channelId, Pageable pageable);

//    List<ChatDTO> mountChatFromDioalogBySessionIdAndChannelId(String sessionId, String channelId);

    Boolean hasDialogByUuid(String uuid);

    Page<ChatDTO> getPaginatedMessages(String sessionId, String channelId, Pageable pageable);
}
