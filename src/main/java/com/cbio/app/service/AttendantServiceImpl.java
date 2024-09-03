package com.cbio.app.service;

import com.cbio.app.entities.CompanyEntity;
import com.cbio.app.entities.UsuarioEntity;
import com.cbio.app.repository.AttendantRepository;
import com.cbio.app.repository.UsuarioRepository;
import com.cbio.app.service.mapper.UsuarioMapper;
import com.cbio.core.service.AttendantService;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.UserService;
import com.cbio.core.v1.dto.UsuarioDTO;
import com.cbio.core.v1.enuns.PerfilEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AttendantServiceImpl implements AttendantService {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final UserService userService;
    private final AuthService authService;

    @Override
    public UsuarioDTO salva(UsuarioDTO.UsuarioFormDTO attendantDTO) {
        attendantDTO.setPerfil(PerfilEnum.ATTENDANT.name());

        Map<String, Object> claimsUserLogged = authService.getClaimsUserLogged();

        Object companyId = claimsUserLogged.get("companyId");

        if(companyId != null) {
            attendantDTO.setCompany(CompanyEntity.builder()
                    .id((String) companyId)
                    .build());
        }else{
            throw new RuntimeException("Companhia não existe no token. Favor contactar os administradores.");
        }


        return userService.salva(attendantDTO, attendantDTO.getPassword(), IAMServiceImpl.ROLE_ATTENDANT);
    }

    @Override
    public UsuarioDTO altera(UsuarioDTO.UsuarioFormDTO attendantDTO) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(attendantDTO.getId())
                .orElseThrow();



        if(StringUtils.hasText(attendantDTO.getPassword())){

            attendantDTO.setEmail(usuarioEntity.getEmail());
            attendantDTO.setName(usuarioEntity.getName());
            attendantDTO.setCompany(usuarioEntity.getCompany());

            return userService.salva(attendantDTO, attendantDTO.getPassword(), IAMServiceImpl.ROLE_ATTENDANT);

        }else{

            usuarioEntity.setEmail(attendantDTO.getEmail());
            usuarioEntity.setName(attendantDTO.getName());
            return usuarioMapper.toDto(usuarioRepository.save(usuarioEntity));
        }
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

    @Override
    public void delete(String id) {
        usuarioRepository.deleteById(id);
    }
}
