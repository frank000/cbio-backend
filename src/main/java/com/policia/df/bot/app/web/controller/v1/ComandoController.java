package com.policia.df.bot.app.web.controller.v1;

import com.policia.df.bot.core.service.ComandoService;
import com.policia.df.bot.core.v1.dto.ComandoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/comando")
public record ComandoController(ComandoService service) {

    @PostMapping(value = "/adicionar")
    public void adicionarComando(ComandoDTO comandoDTO) {
        service.adicionarComando(comandoDTO);
    }

    @GetMapping(value = "/listar")
    public ResponseEntity<List<ComandoDTO>> listarComandos() {

        ResponseEntity resposta = null;

        List<ComandoDTO> lista = service.listarComandos();

        if(lista.isEmpty()) {
            resposta = new ResponseEntity(null, HttpStatus.NO_CONTENT);
        } else {
            resposta = ResponseEntity.ok(lista);
        }

        return resposta;
    }

    @PutMapping(value = "/alterar")
    private void alterar(@RequestBody ComandoDTO comando) throws Exception {

        service.alterar(comando);

    }

}
