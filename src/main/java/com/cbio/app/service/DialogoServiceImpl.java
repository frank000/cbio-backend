package com.cbio.app.service;

import com.cbio.app.repository.DialogoRepository;
import com.cbio.app.service.mapper.DialogoMapper;
import com.cbio.chat.dto.DialogoDTO;
import com.cbio.chat.models.DialogoEntity;
import com.cbio.core.service.DialogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DialogoServiceImpl implements DialogoService {

    private final DialogoRepository dialogoRepository;
    private final DialogoMapper dialogoMapper;

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
}
