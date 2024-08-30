package com.cbio.app.service;

import com.cbio.app.entities.UsuarioEntity;
import com.cbio.app.repository.UsuarioRepository;
import com.cbio.app.service.mapper.UsuarioMapper;
import com.cbio.core.service.AttendantService;
import com.cbio.core.v1.dto.UsuarioDTO;
import com.cbio.core.v1.enuns.PerfilEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AttendantServiceImpl implements AttendantService {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;

    @Override
    public void salva(UsuarioDTO attendantDTO) {
        attendantDTO.setPerfil(PerfilEnum.ATTENDANT.name());

        usuarioRepository.save(usuarioMapper.toEntity(attendantDTO));
    }

    @Override
    public UsuarioDTO buscaPorId(String id) {

        UsuarioEntity usuarioEntity = usuarioRepository
                .findByPerfilAndId(PerfilEnum.ATTENDANT.name(), id)
                .orElseThrow(() -> new RuntimeException("Atendente não encontrado."));

        return usuarioMapper.toDto(usuarioEntity);

    }

    @Override
    public UsuarioDTO fetch() {

        Optional<List<UsuarioEntity>> allByPerfil = usuarioRepository.findAllByPerfil(PerfilEnum.ATTENDANT.name());

        List<UsuarioEntity> usuarioEntities1 = allByPerfil.orElseThrow(() -> new RuntimeException("Nenhum Atendentente encontrado."));
        UsuarioEntity usuarioEntity = usuarioEntities1.stream().findAny().orElseThrow(() -> new RuntimeException("Nenhum Atendentente encontrado."));

        return usuarioMapper.toDto(usuarioEntity);
    }
}
