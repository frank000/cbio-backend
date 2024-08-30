package com.cbio.app.service;

import com.cbio.app.entities.UsuarioEntity;
import com.cbio.app.repository.UsuarioRepository;
import com.cbio.app.service.mapper.UsuarioMapper;
import com.cbio.core.service.IAMService;
import com.cbio.core.service.UserService;
import com.cbio.core.service.UsuarioTelegramService;
import com.cbio.core.v1.dto.UserKeycloak;
import com.cbio.core.v1.dto.UsuarioDTO;
import com.cbio.core.v1.enuns.PerfilEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;

    private final IAMService iamService;

    @Override
    public void salva(UsuarioDTO usuarioDTO, String password) {

        UsuarioEntity entity = usuarioMapper.toEntity(usuarioDTO);
        entity = usuarioRepository.save(entity);
        UserKeycloak userKeycloak = UserKeycloak.builder()
                .userName(entity.getEmail())
                .firstname(entity.getName())
                .idUser(entity.getId())
                .password(password)
                .email(entity.getEmail())
                .build();

        String id = iamService.addUser(userKeycloak, IAMServiceImpl.ROLE_ADMIN);
        entity.setIdKeycloak(id);

        usuarioRepository.save(entity);
    }

    @Override
    public UsuarioDTO buscaPorId(String id) {

        UsuarioEntity usuarioEntity = usuarioRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        return usuarioMapper.toDto(usuarioEntity);

    }

    @Override
    public UsuarioDTO fetch() {
        UsuarioEntity usuarioEntity1 = usuarioRepository.findAll()
                .stream()
                .findAny()
                .orElseThrow(() -> new RuntimeException("Nenhum usuário encontrado."));


        return usuarioMapper.toDto(usuarioEntity1);
    }

}
