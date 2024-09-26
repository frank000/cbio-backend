package com.cbio.app.service;

import com.cbio.app.base.utils.DateRocketUtils;
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
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<DialogoDTO> getAllBySender(String identificadorRementente) {
        List<DialogoEntity> allByIdentificadorRemetente = dialogoRepository.findAllByIdentificadorRemetenteOrderByCreatedDateTime(identificadorRementente);

        return dialogoMapper.toDto(allByIdentificadorRemetente);
    }

    public List<ChatDTO> mountChatFromDioalogByChannelId(String channelId) {
        ChatChannelEntity chatChannelEntity = chatChannelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel não encontrado."));

        SessaoEntity sessionByChannelId = sessaoService.getSessionByChannelId(channelId);
        List<DialogoDTO> allBySender = getAllBySender(String.valueOf(sessionByChannelId.getIdentificadorUsuario()));

        List<ChatDTO> collect = allBySender.stream().map(dialogoDTO -> {

                    if (AssistentEnum.RASA.name().equals(dialogoDTO.getFrom()) ||
                            AssistentEnum.ATTENDANT.name().equals(dialogoDTO.getFrom())) {

                        return ChatDTO.builder()
                                .text(dialogoDTO.getMensagem())
                                .type(dialogoDTO.getType())
                                .id(dialogoDTO.getId())
                                .media(dialogoDTO.getMedia())
                                .fromUserId(chatChannelEntity.getUserTwo().getUuid())
                                .toUserId(chatChannelEntity.getUserOne().getUuid())
                                .time(DateRocketUtils.getDateTimeWithSecFormated(dialogoDTO.getCreatedDateTime()))
                                .build();
                    } else {
                        return ChatDTO.builder()
                                .text(dialogoDTO.getMensagem())
                                .type(dialogoDTO.getType())
                                .id(dialogoDTO.getId())
                                .media(dialogoDTO.getMedia())
                                .toUserId(chatChannelEntity.getUserTwo().getUuid())
                                .fromUserId(chatChannelEntity.getUserOne().getUuid())
                                .time(DateRocketUtils.getDateTimeWithSecFormated(dialogoDTO.getCreatedDateTime()))
                                .build();
                    }

                })
                .collect(Collectors.toList());
        return collect;
    }



}
