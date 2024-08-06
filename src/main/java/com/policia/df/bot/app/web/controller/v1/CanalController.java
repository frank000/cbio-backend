package com.policia.df.bot.app.web.controller.v1;

import com.policia.df.bot.app.entities.CanalEntity;
import com.policia.df.bot.core.service.CanalService;
import com.policia.df.bot.core.v1.dto.CanalDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/canal")
public record CanalController(CanalService service) {

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
