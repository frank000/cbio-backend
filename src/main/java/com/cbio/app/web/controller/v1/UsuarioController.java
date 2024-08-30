package com.cbio.app.web.controller.v1;

import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.AttendantService;
import com.cbio.core.service.UserService;
import com.cbio.core.v1.dto.UsuarioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/user")
public class UsuarioController implements SecuredRestController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody UsuarioDTO.UsuarioFormDTO usuarioDTO) {
        usuarioDTO.setId(null);
        userService.salva(usuarioDTO, usuarioDTO.getPassword());

        return ResponseEntity.ok().build();
    }
    @GetMapping("{id}")
    public ResponseEntity<UsuarioDTO> getById(@PathVariable String id) {

        return ResponseEntity
                .ok( userService.buscaPorId(id));
    }
}
