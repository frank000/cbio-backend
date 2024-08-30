package com.cbio.app.web.controller.v1;

import com.cbio.app.entities.CanalEntity;
import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.CanalService;
import com.cbio.core.v1.dto.CanalDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/canal")
public record CanalController(CanalService service) implements SecuredRestController {

    @PostMapping(value = "/incluir")
    public CanalEntity incluirCanal(@RequestBody CanalEntity canal) {

        return service.incluirCanal(canal);

    }

    @GetMapping(value = "/listar-todos-canais")
    List<CanalEntity> listTabCanal() {
        return service.listarTodos();
    }

    @PutMapping(value = "alterar")
    private void alterar(@RequestBody CanalDTO canal) throws Exception {

        service.alterar(canal);

    }
    @DeleteMapping(value = "/deleta/{id}")
    private void deleta( @Valid @PathVariable("id") String id) {

        service.deleta(id);

    }

}
