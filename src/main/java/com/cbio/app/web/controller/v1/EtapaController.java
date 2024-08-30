package com.cbio.app.web.controller.v1;

import com.cbio.app.web.SecuredRestController;
import com.cbio.core.service.EtapaService;
import com.cbio.core.v1.dto.EtapaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/etapa")
public record EtapaController(EtapaService service) implements SecuredRestController {

    @PostMapping(value = "/adicionar")
    private void adicionarEtapa(EtapaDTO etapaDTO) {
        service.adicionarEtapa(etapaDTO);
    }

    @GetMapping(value = "/listar")
    private ResponseEntity<List<EtapaDTO>> listar() {

        List<EtapaDTO> lista = service.listar();

        return !lista.isEmpty() ? ResponseEntity.ok(lista) : new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/alterar")
    private void alterar(@RequestBody EtapaDTO etapa) throws Exception {

        service.alterar(etapa);

    }

}
