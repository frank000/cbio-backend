package com.policia.df.bot.app.web.controller.v1;

import com.policia.df.bot.core.service.EtapaService;
import com.policia.df.bot.core.v1.dto.EtapaDTO;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/etapa")
public record EtapaController(EtapaService etapaService) {

    @PutMapping(value = "/adicionar")
    public void adicionarEtapa(EtapaDTO etapaDTO) {
        etapaService.adicionarEtapa(etapaDTO);
    }

}
