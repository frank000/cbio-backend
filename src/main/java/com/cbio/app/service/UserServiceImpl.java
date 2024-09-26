package com.cbio.app.service;

import com.cbio.app.entities.UsuarioEntity;
import com.cbio.app.repository.UsuarioRepository;
import com.cbio.app.service.mapper.UsuarioMapper;
import com.cbio.core.service.IAMService;
import com.cbio.core.service.UserService;
import com.cbio.core.v1.dto.UserKeycloak;
import com.cbio.core.v1.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;

    private final IAMService iamService;

    @Override
    public UsuarioDTO salva(UsuarioDTO usuarioDTO, String password, String role) {

        UsuarioEntity entity = usuarioMapper.toEntity(usuarioDTO);
        entity = usuarioRepository.save(entity);
        UserKeycloak userKeycloak = UserKeycloak.builder()
                .userName(entity.getEmail())
                .firstname(entity.getName())
                .idUser(entity.getId())
                .idCompany(entity.getCompany().getId())
                .password(password)
                .email(entity.getEmail())
                .build();

        String id = iamService.addUser(userKeycloak, role);
        entity.setIdKeycloak(id);

        UsuarioEntity save = usuarioRepository.save(entity);
        return usuarioMapper.toDto(save);
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

    @Override
    public void update(String id, UsuarioDTO.UsuarioFormDTO usuarioDTO) {
        UsuarioEntity usuarioEntity = usuarioRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        usuarioMapper.fromDto(usuarioDTO, usuarioEntity);


    }
}
