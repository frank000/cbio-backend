package com.cbio.app.service;

import com.cbio.app.entities.UsuarioEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.UsuarioRepository;
import com.cbio.app.service.mapper.UsuarioMapper;
import com.cbio.core.service.IAMService;
import com.cbio.core.service.UserService;
import com.cbio.core.v1.dto.UserKeycloak;
import com.cbio.core.v1.dto.UsuarioDTO;
import com.cbio.core.v1.enuns.PerfilEnum;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;

    private final IAMService iamService;

    @Override
    public UsuarioDTO salva(UsuarioDTO usuarioDTO, String password, String role) {
        usuarioDTO.setActive(Boolean.TRUE);


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
    public UsuarioDTO update(UsuarioDTO usuarioDTO, String password, String role) {

        try {
            UserKeycloak.UserKeycloakBuilder builder = UserKeycloak.builder();
            usuarioDTO.setActive(Boolean.TRUE);
            UsuarioEntity usuarioEntity = usuarioRepository.findById(usuarioDTO.getId())
                    .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
            builder.oldUserName(usuarioEntity.getEmail());

            usuarioMapper.fromDto(usuarioDTO, usuarioEntity);

            usuarioEntity = usuarioRepository.save(usuarioEntity);


            builder.userName(usuarioEntity.getEmail())
                    .firstname(usuarioEntity.getName())
                    .idUser(usuarioEntity.getId())
                    .idCompany(usuarioEntity.getCompany().getId())
                    .password(password)
                    .email(usuarioEntity.getEmail())
                    .id(usuarioEntity.getIdKeycloak())
                    .build();
            iamService.updateUser(builder.build(), usuarioEntity.getIdKeycloak());

            return usuarioMapper.toDto(usuarioEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePassword(UsuarioDTO usuarioDTO, String password) throws CbioException {

        try {
            UsuarioDTO usuarioDTO1 = this.buscaPorId(usuarioDTO.getId());
            UsuarioEntity usuarioEntity = usuarioRepository.findById(usuarioDTO.getId())
                    .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

            UserKeycloak userKeycloak = UserKeycloak.builder()
                    .userName(usuarioEntity.getEmail())
                    .password(password)
                    .id(usuarioEntity.getIdKeycloak())
                    .build();

            iamService.updateUserPassword(userKeycloak, usuarioEntity.getIdKeycloak());

        } catch (Exception e) {
            String message = String.format("Erro na alteração de senha: %s", e.getMessage());
            throw new CbioException(message, HttpStatus.BAD_REQUEST.value());
        }
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

    @Override
    public UsuarioDTO adminByCompany(String id) {
        UsuarioEntity usuarioEntity = usuarioRepository.findByCompanyIdAndPerfilContainingIgnoreCase(id, PerfilEnum.ADMIN.name())
                .orElseThrow(() -> new NotFoundException("Administrador não encontrado."));
        return usuarioMapper.toDto(usuarioEntity);
    }
}
