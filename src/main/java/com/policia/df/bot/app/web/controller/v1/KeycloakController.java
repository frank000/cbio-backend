package com.policia.df.bot.app.web.controller.v1;

import com.policia.df.bot.core.service.KeycloakService;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import lombok.Data;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/keycloak")
@Data
public class KeycloakController {

    private final KeycloakService service;

    @GetMapping("/user/search")
    ResponseEntity<List<UserRepresentation>> pesquisarUsuarios(
            @Valid @RequestParam String nome) {
        ResponseEntity entity = null;

        List<UserRepresentation> response = service.pesquisarUsuario(nome).get();

        if(response.isEmpty() || response.size() == 0) {
            entity = new ResponseEntity(null, HttpStatus.NO_CONTENT);
        } else {
            entity = ResponseEntity.ok(response);
        }

        return entity;
    }

    @DeleteMapping("/user/delete/{id}")
    ResponseEntity<Void> deletarUsuario(
            @Valid @PathVariable("id") String id) {

          service.deletarUsuario(id);

        return ResponseEntity.ok().build();
    }


}
