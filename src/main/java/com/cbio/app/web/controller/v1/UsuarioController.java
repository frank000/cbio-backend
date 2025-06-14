package com.cbio.app.web.controller.v1;

import com.cbio.app.exception.CbioException;
import com.cbio.app.service.IAMServiceImpl;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.CompanyService;
import com.cbio.core.service.SessaoService;
import com.cbio.core.service.UserService;
import com.cbio.core.v1.dto.ContactDTO;
import com.cbio.core.v1.dto.UsuarioDTO;
import com.cbio.core.v1.enuns.PerfilEnum;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/user")
public class UsuarioController implements SecuredRestController {

    private final UserService userService;
    private final SessaoService sessaoService;
    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody UsuarioDTO.UsuarioFormDTO usuarioDTO) {
        usuarioDTO.setId(null);
        usuarioDTO.setPerfil(PerfilEnum.ADMIN.name());
        userService.salva(usuarioDTO, usuarioDTO.getPassword(), IAMServiceImpl.ROLE_ADMIN);

        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody UsuarioDTO.UsuarioFormDTO usuarioDTO) {

        usuarioDTO.setPerfil(PerfilEnum.ADMIN.name());
        userService.update(usuarioDTO, usuarioDTO.getPassword(), IAMServiceImpl.ROLE_ADMIN);

        return ResponseEntity.ok().build();
    }
    @PostMapping("profile")
    public ResponseEntity<Void> saveProfile(@RequestBody UsuarioDTO.UsuarioFormDTO usuarioDTO) throws MessagingException {

        usuarioDTO.setId(null);
        usuarioDTO.setPerfil(PerfilEnum.ADMIN.name());
        userService.salva(usuarioDTO, usuarioDTO.getPassword(), IAMServiceImpl.ROLE_ADMIN);
        companyService.completeProfile(usuarioDTO.getCompany().getId(), usuarioDTO.getPassword());


        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(@RequestBody UsuarioDTO.UsuarioFormDTO usuarioDTO) throws CbioException {

        userService.updatePassword(usuarioDTO, usuarioDTO.getPassword());

        return ResponseEntity.ok().build();
    }


    @GetMapping("{id}")
    public ResponseEntity<UsuarioDTO> getById(@PathVariable String id) {

        return ResponseEntity
                .ok( userService.buscaPorId(id));
    }
    @GetMapping("/admin-by-company/{companyId}")
    public ResponseEntity<UsuarioDTO> getAdminByCompany(@PathVariable String companyId) {

        return ResponseEntity
                .ok( userService.adminByCompany(companyId));
    }

    @PutMapping("session/{idSession}")
    public ResponseEntity<Void> update(@PathVariable String idSession, @RequestBody UsuarioDTO.UsuarioSessionFormDTO usuarioDTO) {
        sessaoService.updateUserInfosIntoSession(idSession, usuarioDTO);
        return ResponseEntity.ok().build();

    }
    @PutMapping("session/{idSession}/bind")
    public ResponseEntity<Void> bindContacts(@PathVariable String idSession, @RequestBody ContactDTO dto) {
        sessaoService.bindContactToSession(idSession, dto);
        return ResponseEntity.ok().build();

    }
}
