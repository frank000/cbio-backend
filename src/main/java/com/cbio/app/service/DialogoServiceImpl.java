package com.cbio.app.service;

import com.cbio.app.base.utils.CbioDateUtils;
import com.cbio.app.entities.SessaoEntity;
import com.cbio.app.repository.DialogoRepository;
import com.cbio.app.service.enuns.AssistentEnum;
import com.cbio.app.service.mapper.DialogoMapper;
import com.cbio.chat.dto.ChatDTO;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.chat.models.ChatChannelEntity;
import com.cbio.chat.models.DialogoEntity;
import com.cbio.chat.repositories.ChatChannelRepository;
import com.cbio.core.service.DialogoService;
import com.cbio.core.service.SessaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DialogoServiceImpl implements DialogoService {

    private final DialogoRepository dialogoRepository;
    private final DialogoMapper dialogoMapper;

    private final ChatChannelRepository chatChannelRepository;
    private final SessaoService sessaoService;

    @Override
    public DialogoDTO saveDialogo(DialogoDTO dialogo) {

        DialogoEntity dialogoEntity = dialogoMapper.toEntity(dialogo);
        return dialogoMapper.toDto( dialogoRepository.save(dialogoEntity));
    }


    @Override
    public DialogoDTO getById(String id) {
        return dialogoMapper.toDto(dialogoRepository.findById(id).orElseThrow());
    }

    @Override
    public List<DialogoDTO> getAll() {
        List<DialogoEntity> all = dialogoRepository.findAll();
        return dialogoMapper.toDto(all);
    }

    @Override
    public DialogoDTO updateDialogo(DialogoDTO dialogo) {
        DialogoEntity dialogoEntity = dialogoRepository.findById(dialogo.getId())
                .orElseThrow(() -> new IllegalArgumentException("Dialogo não encontrado."));


        dialogoMapper.fromDto(dialogo, dialogoEntity);
        return dialogoMapper.toDto(dialogoRepository.save(dialogoEntity));
    }

    @Override
    public Page<DialogoEntity> getAllBySender(String sessionId, Pageable pageable) {
        Page<DialogoEntity> allBySessionIdOrderByCreatedDateTime = dialogoRepository.findAllBySessionIdOrderByCreatedDateTime(sessionId, pageable);

        return allBySessionIdOrderByCreatedDateTime;
    }

    @Override
    public Page<ChatDTO> mountChatFromDioalogBySessionIdAndChannelIdPaginated(String sessionId, String channelId, Pageable pageable) {


        ChatChannelEntity chatChannelEntity = chatChannelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel não encontrado."));

        SessaoEntity sessionByChannelId = sessaoService.getSessionById(sessionId);
        Page<DialogoEntity> dialogPage = getAllBySender(sessionId, pageable);

        // Mapeia para List<ChatDTO> (igual ao seu código original)
        List<ChatDTO> chatDTOs = dialogPage.stream()
                .map(dialogoMapper::toDto)
                .map(dialogoDTO -> {
                    if (AssistentEnum.RASA.name().equals(dialogoDTO.getFrom()) ||
                            AssistentEnum.ATTENDANT.name().equals(dialogoDTO.getFrom())) {

                        return ChatDTO.builder()
                                .text(dialogoDTO.getMensagem())
                                .type(dialogoDTO.getType())
                                .id(dialogoDTO.getId())
                                .media(dialogoDTO.getMedia())
                                .fromUserId(chatChannelEntity.getUserTwo().getUuid())
                                .toUserId(chatChannelEntity.getUserOne().getUuid())
                                .time(CbioDateUtils.getDateTimeWithSecFormated(dialogoDTO.getCreatedDateTime()))
                                .build();
                    } else {
                        return ChatDTO.builder()
                                .text(dialogoDTO.getMensagem())
                                .type(dialogoDTO.getType())
                                .id(dialogoDTO.getId())
                                .media(dialogoDTO.getMedia())
                                .toUserId(chatChannelEntity.getUserTwo().getUuid())
                                .fromUserId(chatChannelEntity.getUserOne().getUuid())
                                .time(CbioDateUtils.getDateTimeWithSecFormated(dialogoDTO.getCreatedDateTime()))
                                .build();
                    }
                })
                .collect(Collectors.toList());

        // Cria um novo Page<ChatDTO> mantendo a paginação original
        return new PageImpl<>(
                chatDTOs,
                pageable,
                dialogPage.getTotalElements()
        );
    }


    @Override
    public Boolean hasDialogByUuid(String uuid) {
        return dialogoRepository.countDialogoEntitiesByUuid(uuid) > 0 ? Boolean.TRUE : Boolean.FALSE;
    }


    public Page<ChatDTO> getPaginatedMessages(String sessionId, String channelId, Pageable pageable) {
        // Busca todas as mensagens (mantenha sua lógica original)
        Optional<ChatChannelEntity> byId = chatChannelRepository.findById(channelId);


        return mountChatFromDioalogBySessionIdAndChannelIdPaginated(sessionId, channelId, pageable);


    }
}
