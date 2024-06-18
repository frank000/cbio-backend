package com.policia.df.bot.app.service;

import com.policia.df.bot.app.entities.ComandoEntity;
import com.policia.df.bot.app.repository.ComandoRepository;
import com.policia.df.bot.app.repository.EtapaRepository;
import com.policia.df.bot.app.service.mapper.CycleAvoidingMappingContext;
import com.policia.df.bot.app.service.mapper.EtapaMapper;
import com.policia.df.bot.core.service.EtapaService;
import com.policia.df.bot.core.v1.dto.EtapaDTO;
import org.springframework.stereotype.Service;

@Service
public record EtapaServiceImpl(EtapaRepository repository, EtapaMapper mapper) implements EtapaService {

    @Override
    public void adicionarEtapa(EtapaDTO etapa) {

        repository.save(mapper.etapaDTOToEtapaEntity(etapa, new CycleAvoidingMappingContext()));
    }
}
