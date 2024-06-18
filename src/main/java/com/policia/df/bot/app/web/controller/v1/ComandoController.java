package com.policia.df.bot.app.web.controller.v1;

import com.policia.df.bot.core.service.ComandoService;
import com.policia.df.bot.core.v1.dto.ComandoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/comando")
public record ComandoController(ComandoService service) {

    @PutMapping(value = "/adicionar")
    public void adicionarComando(ComandoDTO comandoDTO) {
        service.adicionarComando(comandoDTO);
    }

    @GetMapping(value = "/listar")
    public ResponseEntity<List<ComandoDTO>> listarComandos() {
        return ResponseEntity.ok(List.of());
    }


}
