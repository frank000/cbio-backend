package com.cbio.app.service;

import com.cbio.app.entities.CompanyEntity;
import com.cbio.app.entities.UsuarioEntity;
import com.cbio.app.exception.CbioException;
import com.cbio.app.repository.UsuarioRepository;
import com.cbio.app.service.mapper.UsuarioMapper;
import com.cbio.core.service.AttendantService;
import com.cbio.core.service.AuthService;
import com.cbio.core.service.IAMService;
import com.cbio.core.service.UserService;
import com.cbio.core.v1.dto.CompanyDTO;
import com.cbio.core.v1.dto.UsuarioDTO;
import com.cbio.core.v1.enuns.PerfilEnum;
import jakarta.ws.rs.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AttendantServiceImpl implements AttendantService {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final UserService userService;
    private final AuthService authService;
    private final IAMService iamService;
    @Override
    public UsuarioDTO salva(UsuarioDTO.UsuarioFormDTO attendantDTO) throws CbioException {

        try{
            attendantDTO.setPerfil(PerfilEnum.ATTENDANT.name());

            Object companyId = authService.getCompanyIdUserLogged();

           // Integer numAttendantsToCompany = companyService.getNumAttendantsToCompany((String) companyId);
            attendantDTO.setCompany(CompanyDTO.builder()
                    .id((String) companyId)
                    .build());

            return userService.salva(attendantDTO, attendantDTO.getPassword(), IAMServiceImpl.ROLE_ATTENDANT);

        } catch (ForbiddenException e) {
            throw new CbioException("Possívelmente você não tem permissão para essa ação. Certfique-se ser um administrador da companhia: " + e.getMessage(), HttpStatus.FORBIDDEN.value());
        }

    }

    @Override
    public UsuarioDTO altera(UsuarioDTO.UsuarioFormDTO attendantDTO) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(attendantDTO.getId())
                .orElseThrow();



        if(StringUtils.hasText(attendantDTO.getPassword())){

            attendantDTO.setEmail(attendantDTO.getEmail());
            attendantDTO.setName(attendantDTO.getName());
            attendantDTO.setCompany(usuarioEntity.getCompany());
            attendantDTO.setPerfil(PerfilEnum.ATTENDANT.name());
            return userService.update(attendantDTO, attendantDTO.getPassword(), IAMServiceImpl.ROLE_ATTENDANT);

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

        UsuarioEntity usuarioEntity = usuarioRepository.findById(id).orElseThrow();
        usuarioEntity.setActive(Boolean.FALSE);
        usuarioRepository.save(usuarioEntity);

        iamService.deleteUserByUserName(usuarioEntity.getEmail());

    }

    @Override
    public UsuarioDTO findTopByOrderByTotalChatsDistribuidosAsc(String companyId) {
        UsuarioEntity usuarioEntity = usuarioRepository.findTopByCompanyIdAndPerfilAndActiveIsTrueOrderByTotalChatsReceivedAsc(companyId, PerfilEnum.ATTENDANT.name())
                .orElseThrow(() -> new RuntimeException("Nenhum Atendentente encontrado."));

        return usuarioMapper.toDto(usuarioEntity);
    }

    @Override
    public void incrementTotalChatsReceived(UsuarioDTO usuarioDTO) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(usuarioDTO.getId())
                .orElseThrow(() -> new RuntimeException("Nenhum Atendentente encontrado."));
        usuarioEntity.setTotalChatsReceived(usuarioEntity.getTotalChatsReceived() + 1);
        usuarioRepository.save(usuarioEntity);
    }

    @Override
    public Boolean isAttendantActive(String id) {
        return usuarioRepository.countByIdAndActiveIsTrue(id) > 0? Boolean.TRUE : Boolean.FALSE;
    }

}
