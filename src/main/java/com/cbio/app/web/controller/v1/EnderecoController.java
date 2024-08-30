package com.cbio.app.web.controller.v1;

import com.cbio.app.client.IbgeRestClient;
import com.cbio.core.v1.dto.ComandoDTO;
import com.cbio.core.v1.dto.SelecaoDTO;
import com.cbio.core.v1.enuns.EstadosEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/v1/endereco")
@RequiredArgsConstructor
public class EnderecoController {

    private final IbgeRestClient ibgeRestClient;

    @GetMapping(value = "/ufs")
    public ResponseEntity<List<SelecaoDTO>> ufs() {
        List<SelecaoDTO> collect = Arrays.stream(EstadosEnum.values())
                .map(estadosEnum -> SelecaoDTO.builder()
                        .nome(estadosEnum.getNome())
                        .id(estadosEnum.name())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(collect);
    }
    @GetMapping(value = "/cidades/{uf}")
    public ResponseEntity<List<SelecaoDTO>> cidades(@PathVariable String uf) {

        EstadosEnum estadosEnum1 = Arrays.stream(EstadosEnum.values())
                .filter(estadosEnum -> estadosEnum.name().equals(uf))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Estado não encontrado."));

        List<SelecaoDTO> cidades = ibgeRestClient.getCidades(estadosEnum1.getId());
        return ResponseEntity.ok(cidades);
    }
}

